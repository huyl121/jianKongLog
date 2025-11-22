package org.other;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.example.PrivateConfig;
import sun.util.resources.LocaleData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

// 交易记录类
class TradeRecord {
    private LocalDate date;
    private double capital;  // 投资金额
    private double profit;   // 盈亏金额
    
    public TradeRecord(LocalDate date, double capital, double profit) {
        this.date = date;
        this.capital = capital;
        this.profit = profit;
    }
    
    // Getters
    public LocalDate getDate() { return date; }
    public double getCapital() { return capital; }
    public double getProfit() { return profit; }
    public double getReturnRate() { return profit / capital; }
}

// 交易员类
class Trader {
    private String name;
    private List<TradeRecord> trades;
    
    public Trader(String name, List<TradeRecord> trades) {
        this.name = name;
        this.trades = trades;
    }
    
    // Getters
    public String getName() { return name; }
    public List<TradeRecord> getTrades() { return trades; }
}

// 交易员评估结果类
class TraderEvaluation {
    private String traderName;
    private double annualReturn;
    private double sharpeRatio;
    private double maxDrawdown;
    private double compositeScore;
    private int rank;
    
    public TraderEvaluation(String traderName, double annualReturn, 
                          double sharpeRatio, double maxDrawdown, 
                          double compositeScore) {
        this.traderName = traderName;
        this.annualReturn = annualReturn;
        this.sharpeRatio = sharpeRatio;
        this.maxDrawdown = maxDrawdown;
        this.compositeScore = compositeScore;
    }
    
    // Getters and Setters
    public String getTraderName() { return traderName; }
    public double getAnnualReturn() { return annualReturn; }
    public double getSharpeRatio() { return sharpeRatio; }
    public double getMaxDrawdown() { return maxDrawdown; }
    public double getCompositeScore() { return compositeScore; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    
    @Override
    public String toString() {
        return String.format("%s | 年化: %.2f%% | 夏普: %.2f | 最大回撤: %.2f%% | 综合: %.2f | 排名: %d",
                traderName, annualReturn * 100, sharpeRatio, maxDrawdown * 100, compositeScore, rank);
    }
}

// 交易员评估器
class TraderEvaluator {
    private static final double RISK_FREE_RATE = 0.02; // 无风险利率 2%
    private static final int TRADING_DAYS_PER_YEAR = 365; // 年交易天数
    
    // 计算年化收益率
    public static double calculateAnnualReturn(List<TradeRecord> trades) {
        if (trades.isEmpty()) return 0;
        
        // 按日期排序
        trades.sort(Comparator.comparing(TradeRecord::getDate));
        
        LocalDate startDate = trades.get(0).getDate();
        LocalDate endDate = trades.get(trades.size() - 1).getDate();
        
        // 计算总天数
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (totalDays <= 0) return 0;
        
        // 计算总收益率
        double totalCapital = trades.stream().mapToDouble(TradeRecord::getCapital).sum();
        double totalProfit = trades.stream().mapToDouble(TradeRecord::getProfit).sum();
        double totalReturn = totalProfit / totalCapital;
        
        // 年化收益率
        return Math.pow(1 + totalReturn, (double) TRADING_DAYS_PER_YEAR / totalDays) - 1;
    }
    
    // 计算夏普比率
    public static double calculateSharpeRatio(List<TradeRecord> trades) {
        if (trades.size() < 2) return 0;
        
        // 计算每笔交易的收益率
        double[] returns = trades.stream()
                .mapToDouble(TradeRecord::getReturnRate)
                .toArray();
        
        // 计算平均收益率和标准差
        double meanReturn = Arrays.stream(returns).average().orElse(0);
        double stdDev = calculateStandardDeviation(returns);
        
        if (stdDev == 0) return 0;
        
        // 年化夏普比率
        return (meanReturn - RISK_FREE_RATE / TRADING_DAYS_PER_YEAR) / stdDev * Math.sqrt(TRADING_DAYS_PER_YEAR);
    }
    
    // 计算最大回撤
    public static double calculateMaxDrawdown(List<TradeRecord> trades) {
        if (trades.isEmpty()) return 0;
        
        // 按日期排序
        trades.sort(Comparator.comparing(TradeRecord::getDate));
        
        // 模拟资金曲线（简化版，假设每笔投资独立）
        double cumulativeReturn = 0;
        double peak = 0;
        double maxDrawdown = 0;
        
        for (TradeRecord trade : trades) {
            // 假设每笔投资对组合贡献相同权重的收益
            double portfolioContribution = trade.getReturnRate() / trades.size();
            cumulativeReturn += portfolioContribution;
            
            // 更新峰值和回撤
            if (cumulativeReturn > peak) {
                peak = cumulativeReturn;
            }
            
            double drawdown = (peak - cumulativeReturn) / (1 + peak);
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown;
            }
        }
        
        return maxDrawdown;
    }
    
    // 计算标准差
    private static double calculateStandardDeviation(double[] values) {
        double mean = Arrays.stream(values).average().orElse(0);
        double variance = Arrays.stream(values)
                .map(val -> Math.pow(val - mean, 2))
                .average().orElse(0);
        return Math.sqrt(variance);
    }
    
    // 综合评估交易员
    public static TraderEvaluation evaluateTrader(Trader trader) {
        List<TradeRecord> trades = trader.getTrades();
        
        double annualReturn = calculateAnnualReturn(trades);
        double sharpeRatio = calculateSharpeRatio(trades);
        double maxDrawdown = calculateMaxDrawdown(trades);
        
        // 计算综合评分（年化收益40%，夏普比率35%，最大回撤25%）
        double returnScore = normalizeReturn(annualReturn);
        double sharpeScore = normalizeSharpe(sharpeRatio);
        double drawdownScore = normalizeDrawdown(maxDrawdown);
        
        double compositeScore = returnScore * 0.40 + sharpeScore * 0.35 + drawdownScore * 0.25;
        
        return new TraderEvaluation(trader.getName(), annualReturn, sharpeRatio, maxDrawdown, compositeScore);
    }
    
    // 标准化年化收益率到0-10分
    private static double normalizeReturn(double annualReturn) {
        if (annualReturn >= 0.5) return 10;  // 50%以上给满分
        if (annualReturn >= 0.3) return 8;   // 30%-50%
        if (annualReturn >= 0.2) return 7;   // 20%-30%
        if (annualReturn >= 0.15) return 6;  // 15%-20%
        if (annualReturn >= 0.1) return 5;   // 10%-15%
        if (annualReturn >= 0.05) return 4;  // 5%-10%
        if (annualReturn >= 0) return 3;     // 0%-5%
        if (annualReturn >= -0.1) return 2;  // -10%-0%
        return 1;                            // 低于-10%
    }
    
    // 标准化夏普比率到0-10分
    private static double normalizeSharpe(double sharpeRatio) {
        if (sharpeRatio >= 2.0) return 10;   // 2.0以上给满分
        if (sharpeRatio >= 1.5) return 8;    // 1.5-2.0
        if (sharpeRatio >= 1.2) return 7;    // 1.2-1.5
        if (sharpeRatio >= 1.0) return 6;    // 1.0-1.2
        if (sharpeRatio >= 0.8) return 5;    // 0.8-1.0
        if (sharpeRatio >= 0.5) return 4;    // 0.5-0.8
        if (sharpeRatio >= 0.2) return 3;    // 0.2-0.5
        if (sharpeRatio >= 0) return 2;      // 0-0.2
        return 1;                            // 负值
    }
    
    // 标准化最大回撤到0-10分（回撤越小越好）
    private static double normalizeDrawdown(double maxDrawdown) {
        if (maxDrawdown <= 0.05) return 10;  // 5%以内给满分
        if (maxDrawdown <= 0.1) return 8;    // 5%-10%
        if (maxDrawdown <= 0.15) return 7;   // 10%-15%
        if (maxDrawdown <= 0.2) return 6;    // 15%-20%
        if (maxDrawdown <= 0.25) return 5;   // 20%-25%
        if (maxDrawdown <= 0.3) return 4;    // 25%-30%
        if (maxDrawdown <= 0.4) return 3;    // 30%-40%
        if (maxDrawdown <= 0.5) return 2;    // 40%-50%
        return 1;                            // 超过50%
    }
}

// 主程序
public class deepseek {
    public static void main(String[] args) {
        // 创建示例交易员数据
        List<Trader> traders = createSampleTraders();
        
        // 评估所有交易员
        List<TraderEvaluation> evaluations = traders.stream()
                .map(TraderEvaluator::evaluateTrader)
                .collect(Collectors.toList());
        
        // 按综合评分排序并设置排名
        evaluations.sort((a, b) -> Double.compare(b.getCompositeScore(), a.getCompositeScore()));
        for (int i = 0; i < evaluations.size(); i++) {
            evaluations.get(i).setRank(i + 1);
        }
        
        // 输出结果
        printEvaluationResults(evaluations);
        
        // 输出详细分析
        printDetailedAnalysis(evaluations);
    }
    
    // 创建示例交易员数据
    private static List<Trader> createSampleTraders() {
        List<Trader> traders = new ArrayList<>();

        JSONObject config = PrivateConfig.readJsonFile("E://JianKongLog//JianKongLog//src//main//java//org//other//data.json");
        JSONArray jiaoYiYuan = config.getJSONArray("data");
        for(Object object : jiaoYiYuan){
            JSONObject jsonObject = (JSONObject) object;
            String name = jsonObject.getString("name");
            List<TradeRecord> trades = new ArrayList<>();
            JSONArray jiaoYi =  jsonObject.getJSONArray("jiaoYi");
            for(Object object1 : jiaoYi){
                String jiaoYi1 = (String) object1;
                trades.add(new TradeRecord(LocalDate.parse(jiaoYi1.split(",")[0]), Double.parseDouble(jiaoYi1.split(",")[1]), Double.parseDouble(jiaoYi1.split(",")[2])));
            }
            traders.add(new Trader(name, trades));
        }




        /*Random random = new Random(42); // 固定随机种子以便重现结果
        
        // 交易员A：稳健型（6个月数据）
        traders.add(createTrader("稳健型-张三", 180, 0.02, 0.008, random, 0.7));
        
        // 交易员B：成长型（9个月数据）
        traders.add(createTrader("成长型-李四", 270, 0.03, 0.015, random, 0.6));
        
        // 交易员C：保守型（6个月数据）
        traders.add(createTrader("保守型-王五", 180, 0.015, 0.005, random, 0.8));
        
        // 交易员D：激进型（9个月数据）
        traders.add(createTrader("激进型-赵六", 270, 0.04, 0.025, random, 0.5));*/
        
        return traders;
    }
    
    // 创建交易员交易记录
    private static Trader createTrader(String name, int days, double baseReturn, 
                                     double volatility, Random random, double winProbability) {
        List<TradeRecord> trades = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusDays(days);
        
        for (int i = 0; i < days / 3; i++) { // 每3天交易一次
            LocalDate tradeDate = startDate.plusDays(i * 3);
            double capital = 50000 + random.nextDouble() * 100000; // 5万-15万
            
            double profit;
            if (random.nextDouble() < winProbability) {
                // 盈利交易
                profit = capital * (baseReturn + random.nextGaussian() * volatility / 2);
            } else {
                // 亏损交易
                profit = -capital * (baseReturn + Math.abs(random.nextGaussian()) * volatility);
            }
            
            trades.add(new TradeRecord(tradeDate, capital, profit));
        }
        
        return new Trader(name, trades);
    }
    
    // 打印评估结果
    private static void printEvaluationResults(List<TraderEvaluation> evaluations) {
        System.out.println("==================================================");
        System.out.println("             股票交易员综合评估报告");
        System.out.println("==================================================");
        System.out.println();
        
        System.out.println("排名结果:");
        System.out.println("--------------------------------------------------");
        for (TraderEvaluation eval : evaluations) {
            System.out.println(eval);
        }
        System.out.println();
    }
    
    // 打印详细分析
    private static void printDetailedAnalysis(List<TraderEvaluation> evaluations) {
        System.out.println("详细分析:");
        System.out.println("--------------------------------------------------");
        
        for (TraderEvaluation eval : evaluations) {
            System.out.printf("%s 分析:%n", eval.getTraderName());
            System.out.printf("  📈 年化收益率: %.2f%%", eval.getAnnualReturn() * 100);
            printRating(eval.getAnnualReturn(), 0.15, 0.25, 0.10);
            
            System.out.printf("  ⚡ 夏普比率: %.2f", eval.getSharpeRatio());
            printRating(eval.getSharpeRatio(), 1.2, 1.8, 0.8);
            
            System.out.printf("  🛡️  最大回撤: %.2f%%", eval.getMaxDrawdown() * 100);
            printRating(1 - eval.getMaxDrawdown(), 0.85, 0.90, 0.80); // 回撤越小越好
            
            System.out.printf("  🎯 综合评分: %.2f/10", eval.getCompositeScore());
            printRating(eval.getCompositeScore(), 7.0, 8.5, 6.0);
            
            // 给出投资建议
            System.out.printf("  💡 投资建议: %s%n", getInvestmentAdvice(eval));
            System.out.println();
        }
    }
    
    // 打印评级
    private static void printRating(double value, double excellent, double outstanding, double good) {
        if (value >= outstanding) {
            System.out.println(" (卓越)");
        } else if (value >= excellent) {
            System.out.println(" (优秀)");
        } else if (value >= good) {
            System.out.println(" (良好)");
        } else {
            System.out.println(" (一般)");
        }
    }
    
    // 获取投资建议
    private static String getInvestmentAdvice(TraderEvaluation eval) {
        double annualReturn = eval.getAnnualReturn();
        double sharpeRatio = eval.getSharpeRatio();
        double maxDrawdown = eval.getMaxDrawdown();
        
        if (annualReturn >= 0.25 && sharpeRatio >= 1.5 && maxDrawdown <= 0.15) {
            return "强烈推荐 - 高收益低风险";
        } else if (annualReturn >= 0.15 && sharpeRatio >= 1.0 && maxDrawdown <= 0.2) {
            return "推荐 - 收益风险平衡良好";
        } else if (annualReturn >= 0.10 && sharpeRatio >= 0.8 && maxDrawdown <= 0.25) {
            return "可考虑 - 表现稳定";
        } else if (annualReturn >= 0.05) {
            return "谨慎考虑 - 收益偏低或风险较高";
        } else {
            return "暂不推荐 - 需要改进";
        }
    }
}