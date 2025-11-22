package org.other;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.example.PrivateConfig;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TraderEvaluator1 {

        // 一条交易记录
        static class Trade {
            LocalDate date;
            double capital;      // 这单投入的资金（本金）
            double profit;       // 这单的盈亏（正数盈利，负数亏损）

            Trade(LocalDate date, double capital, double profit) {
                this.date = date;
                this.capital = capital;
                this.profit = profit;
            }
        }

        // 交易员绩效结果
        static class Result implements Comparable<Result> {
            String name;
            double annualizedReturn;    // 年化收益率
            double sharpe;              // 夏普比率
            double sortino;             // 索提诺比率
            double maxDrawdown;         // 最大回撤（负数）
            double calmar;              // 年化/Calmar
            double profitFactor;        // 盈利因子
            double totalProfit;         // 总盈亏
            int days;                   // 交易覆盖天数

            @Override
            public int compareTo(Result o) {
                // 主排序：Calmar → 夏普 → 年化收益 → 总盈亏
                if (Math.abs(this.calmar - o.calmar) > 0.01) {
                    return Double.compare(o.calmar, this.calmar);
                }
                if (Math.abs(this.sharpe - o.sharpe) > 0.01) {
                    return Double.compare(o.sharpe, this.sharpe);
                }
                if (Math.abs(this.annualizedReturn - o.annualizedReturn) > 0.001) {
                    return Double.compare(o.annualizedReturn, this.annualizedReturn);
                }
                return Double.compare(o.totalProfit, this.totalProfit);
            }

            @Override
            public String toString() {
                return String.format("%-8s | 年化 %.2f%% | 夏普 %.3f | Sortino %.3f | 最大回撤 %.2f%% | Calmar %.2f | PF %.2f | 总盈亏 %.0f | 天数 %d",
                        name,
                        annualizedReturn * 100,
                        sharpe,
                        sortino,
                        maxDrawdown * 100,
                        calmar,
                        profitFactor,
                        totalProfit,
                        days);
            }
        }

    // 把下面整个 evaluate 方法替换你原来的即可（已修复 + 优化 + 加了详细注释）
    public static Result evaluate(String name, List<Trade> trades, double initialCapital) {
        if (trades.isEmpty()) throw new IllegalArgumentException("没有交易记录");

        trades.sort(Comparator.comparing(t -> t.date));

        LocalDate firstDate = trades.get(0).date;
        LocalDate lastDate = trades.get(trades.size() - 1).date;
        long totalDays = ChronoUnit.DAYS.between(firstDate, lastDate) + 1;

        // 合并同一天的多单
        Map<LocalDate, Double> dailyProfitMap = new HashMap<>();
        for (Trade t : trades) {
            dailyProfitMap.merge(t.date, t.profit, Double::sum);
        }

        // 生成每日净值曲线
        List<Double> equityCurve = new ArrayList<>();
        double equity = initialCapital;
        double peak = initialCapital;
        double totalWin = 0, totalLoss = 0;

        LocalDate date = firstDate;
        while (!date.isAfter(lastDate)) {
            double dailyProfit = dailyProfitMap.getOrDefault(date, 0.0);
            if (dailyProfit > 0) totalWin += dailyProfit;
            if (dailyProfit < 0) totalLoss += -dailyProfit;

            equity += dailyProfit;
            equityCurve.add(equity);

            peak = Math.max(peak, equity);
            date = date.plusDays(1);
        }

        double totalProfit = equity - initialCapital;
        double totalReturn = totalProfit / initialCapital;
        double years = totalDays / 365.0;
        double annualizedReturn = years > 0 ? Math.pow(1 + totalReturn, 1 / years) - 1 : totalReturn;

        // 计算日收益率序列（从第2天起）
        List<Double> dailyReturns = new ArrayList<>();
        for (int i = 1; i < equityCurve.size(); i++) {
            double prev = equityCurve.get(i - 1);
            dailyReturns.add((equityCurve.get(i) - prev) / prev);
        }

        // 最大回撤
        double maxDD = 0;
        peak = initialCapital;
        for (double e : equityCurve) {
            if (e > peak) peak = e;
            maxDD = Math.max(maxDD, (peak - e) / peak);
        }

        // 夏普 & 索提诺
        if (dailyReturns.isEmpty()) {
            dailyReturns.add(0.0); // 防止只有1天
        }

        double avgReturn = dailyReturns.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        double variance = dailyReturns.stream()
                .mapToDouble(r -> Math.pow(r - avgReturn, 2))
                .average().getAsDouble();
        double std = Math.sqrt(variance) * Math.sqrt(252); // 年化波动率

        double downsideVariance = dailyReturns.stream()
                .filter(r -> r < 0)
                .mapToDouble(r -> r * r)
                .average().orElse(0.0);
        double downsideStd = Math.sqrt(downsideVariance) * Math.sqrt(252);

        double sharpe = std > 1e-8 ? annualizedReturn / std : 0;
        double sortino = downsideStd > 1e-8 ? annualizedReturn / downsideStd : 0;
        double calmar = maxDD > 1e-8 ? annualizedReturn / maxDD : 0;
        double profitFactor = totalLoss > 1e-8 ? totalWin / totalLoss : (totalWin > 0 ? Double.POSITIVE_INFINITY : 0);

        Result r = new Result();
        r.name = name;
        r.annualizedReturn = annualizedReturn;
        r.sharpe = sharpe;
        r.sortino = sortino;
        r.maxDrawdown = -maxDD;
        r.calmar = calmar;
        r.profitFactor = profitFactor;
        r.totalProfit = totalProfit;
        r.days = (int) totalDays;

        return r;
    }

        // ==================== 请在这里填你的真实数据 ====================
        public static void main(String[] args) {

            List<Result> results = new ArrayList<>();

            JSONObject config = PrivateConfig.readJsonFile("E://JianKongLog//JianKongLog//src//main//java//org//other//data.json");
            JSONArray jiaoYiYuan = config.getJSONArray("data");
            for(Object object : jiaoYiYuan) {
                JSONObject jsonObject = (JSONObject) object;
                String name = jsonObject.getString("name");
                List<Trade> traderA = new ArrayList<>();
                JSONArray jiaoYi = jsonObject.getJSONArray("jiaoYi");
                double initialCapital = 0;
                int jiaoYiCount = 0;
                for (Object object1 : jiaoYi) {
                    String jiaoYi1 = (String) object1;
                    traderA.add(new Trade(LocalDate.parse(jiaoYi1.split(",")[0]), Double.parseDouble(jiaoYi1.split(",")[1]), Double.parseDouble(jiaoYi1.split(",")[2])));
                    initialCapital += Double.parseDouble(jiaoYi1.split(",")[1]);
                    jiaoYiCount++;
                }

                results.add(evaluate(name, traderA, initialCapital/jiaoYiCount*10));
            }
            // 继续添加交易员C、D、E...
            // results.add(evaluate("交易员C", traderCList, 150000));

            // ==================== 输出排名 ====================
            results.sort(Collections.reverseOrder()); // 从优到劣

            System.out.println("==========================================================================================");
            System.out.println("排名    交易员   年化收益率   夏普比率   Sortino   最大回撤     Calmar    盈利因子   总盈亏     交易天数");
            System.out.println("----------------------------------------------------------------------------------------");
            for (int i = 0; i < results.size(); i++) {
                System.out.println(String.format("%-3d    %s", i + 1, results.get(i)));
            }
            System.out.println("==========================================================================================");
            System.out.println("注：综合排名优先级：Calmar > 夏普 > 年化收益 > 总盈亏（已充分年化、风险调整）");
        }
}

