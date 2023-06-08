package xyz.zwxin.work.boxiaotong.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductUtil {


    public static int extractUnit(String productName) {
        // 定义正则表达式
        String regex = "\\d+(\\.\\d+)?[升Llml毫升MLmL]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(productName);

        int unit = 0;
        if (matcher.find()) {
            // 提取数字和单位
            String str = matcher.group();
            double num = Double.parseDouble(str.replaceAll("[升Llml毫升MLmL]", ""));
            String unitStr = str.replaceAll("[0-9.]", "");

            // 判断单位是L还是ml，如果是L，把L转化成ml
            if (unitStr.equalsIgnoreCase("L") || unitStr.equalsIgnoreCase("升")) {
                unit = (int) (num * 1000);
            } else {
                unit = (int) num;
            }
        }
        return unit;
    }

    public double calculateMinPriceUnit(double pagePrice, String promotionInfo) {
        double minPrice = pagePrice; // 初始化最低到手价为页面价格
        List<Promotion> promotions = filterPromotions(pagePrice, promotionInfo);
        int count = 1;
        for (Promotion promotion1 : promotions) {
            if (promotion1.getSpecialPrice() < pagePrice) {//在单件价优惠内，使用promotion1.specialPrice做单价
                for (Promotion promotion2 : promotions) {
                    if (promotion1.getMaxQuantity() * promotion1.getSpecialPrice() < promotion2.getFullPrice()) {//最大数量单件价达不到此次满减
                        if (minPrice > promotion1.getSpecialPrice()) {
                            minPrice = promotion1.getSpecialPrice();
                        }
                    } else {//单件价+此次满减
                        count = promotion1.getMinQuantity();
                        while (count * promotion1.getSpecialPrice() < promotion2.getFullPrice()) {//达不到此次满减条件，增加数量，此时单价是单件价优惠
                            count++;
                        }
                        if (((count * promotion1.getSpecialPrice() - promotion2.getMinusPrice()) / count) < minPrice) {
                            minPrice = (count * promotion1.getSpecialPrice() - promotion2.getMinusPrice()) / count;
                        }
                    }
                }
            } else {//单满减
                for (Promotion promotion2 : promotions) {
                    count = 1;
                    while (count * pagePrice < promotion2.getFullPrice()) {//达不到此次满减条件，增加数量，此时单价是单件价优惠
                        count++;
                    }
                    if (((count * pagePrice - promotion2.getMinusPrice()) / count) < minPrice) {
                        minPrice = ((count * pagePrice - promotion2.getMinusPrice()) / count);
                    }
                }
            }
        }
        return minPrice;

    }


    class Promotion {
        private double fullPrice; // 满减条件金额
        private double minusPrice; // 满减金额
        private double specialPrice; // 单件价优惠金额
        private int minQuantity; // 单件价优惠的最小数量
        private int maxQuantity; // 单件价优惠的最大数量

        public Promotion(double fullPrice, double minusPrice, double pagePrice) {
            this.fullPrice = fullPrice;
            this.minusPrice = minusPrice;
            this.specialPrice = pagePrice;
            this.minQuantity = 0;
            this.maxQuantity = 99999;
        }

        public Promotion(double specialPrice, int minQuantity, int maxQuantity) {
            this.fullPrice = 0;
            this.minusPrice = 0;
            this.specialPrice = specialPrice;
            this.minQuantity = minQuantity;
            this.maxQuantity = maxQuantity;
        }

        public double getFullPrice() {
            return fullPrice;
        }

        public double getMinusPrice() {
            return minusPrice;
        }

        public double getSpecialPrice() {
            return specialPrice;
        }

        public int getMinQuantity() {
            return minQuantity;
        }

        public int getMaxQuantity() {
            return maxQuantity;
        }

    }

    public List<Promotion> filterPromotions(double pagePrice, String promotionInfo) {
        List<Promotion> promotions = new ArrayList<>();
        // 解析促销信息
        String[] promotionParts = promotionInfo.split(",");
        for (String promotion : promotionParts) {
            try {
                if (promotion.contains("满")) { // 满减促销
                    String[] promotionParts2 = promotion.split("减");
                    String fullPriceStr = promotionParts2[0].substring(1);
                    double fullPrice;
                    if (fullPriceStr.contains("元")) {
                        fullPrice = Double.parseDouble(fullPriceStr.replaceAll("[^0-9\\.]", ""));
                    } else {
                        fullPrice = Double.parseDouble(fullPriceStr);
                    }
                    double minusPrice = Double.parseDouble(promotionParts2[1].replaceAll("[^0-9\\.]", ""));
                    promotions.add(new Promotion(fullPrice, minusPrice, pagePrice));
                } else if (promotion.contains("单件价")) { // 单件价促销
                    String[] promotionParts2 = promotion.split("享受单件价¥");
                    String[] promotionParts3 = promotionParts2[0].split("，");
                    String quantityRange = promotionParts3[promotionParts3.length - 1];
                    String[] quantityRangeParts = quantityRange.split("-");
                    int minQuantity = 1;
                    int maxQuantity = 9999;
                    if (quantityRangeParts[0].contains("至少")) {
                        minQuantity = Integer.parseInt(quantityRangeParts[0].substring(2));
                    } else {
                        minQuantity = Integer.parseInt(quantityRangeParts[0].substring(1));
                    }
                    if (quantityRangeParts.length > 1) {
                        maxQuantity = Integer.parseInt(quantityRangeParts[1]);
                    }
                    double specialPrice = Double.parseDouble(promotionParts2[1].replaceAll("[^0-9\\.]", ""));
                    promotions.add(new Promotion(specialPrice, minQuantity, maxQuantity));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid promotion format: " + promotion);
            }
        }
        return promotions;
    }


}

