package xyz.zwxin.work.boxiaotong.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.zwxin.work.boxiaotong.mapper.MappingDictMapper;
import xyz.zwxin.work.boxiaotong.pojo.MappingDict;
import xyz.zwxin.work.boxiaotong.pojo.Product;
import xyz.zwxin.work.boxiaotong.service.impl.ProductServiceImpl;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
class ProductServiceTest {
    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private MappingDictMapper dictMapper;


    @Test
    void updateProductSeries() {
        // 执行商品系列标签更新
        productService.updateProductSeries();

        // 验证商品系列标签是否正确更新
        List<Product> productList = productService.list();
        for (Product product : productList) {
            String brand = product.getBrand();
            String productName = product.getProductName();
            String expectedSeries = getExpectedSeries(brand, productName);
            String actualSeries = product.getSeries();
            Assert.assertEquals(expectedSeries, actualSeries);
        }
    }

    /**
     * 根据品牌和商品名称获取期望的系列标签
     *
     * @param brand       品牌
     * @param productName 商品名称
     * @return 系列标签
     */
    private String getExpectedSeries(String brand, String productName) {
        if (brand.equals("其他")) {
            return "其他";
        }
        // 获取映射字典
        QueryWrapper<MappingDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("brand", brand);
        List<MappingDict> mappingDictList = dictMapper.selectList(queryWrapper);

        // 遍历映射字典
        for (MappingDict mappingDict : mappingDictList) {
            // 获取关键字列表
            List<String> keywords = new ArrayList<>();
            keywords.add(mappingDict.getFirstKeyword());
            keywords.add(mappingDict.getSecondKeyword());
            keywords.add(mappingDict.getThirdKeyword());
            keywords.add(mappingDict.getFourthKeyword());

            // 匹配第一关键字
            if (StringUtils.isNotBlank(keywords.get(0)) && productName.contains(keywords.get(0))) {
                return mappingDict.getMappingValue();
            }

            // 匹配2、3、4关键字
            boolean match = true;
            int count = 0; // 统计非空关键字的数量
            for (int i = 1; i < keywords.size(); i++) {
                String keyword = keywords.get(i);
                if (StringUtils.isNotBlank(keyword)) {
                    count++;
                    if (!productName.contains(keyword)) {
                        match = false;
                        break;
                    }
                }
            }
            if (count == 0) { // 2、3、4关键字都不存在或都为空，不能映射
                match = false;
            }
            if (match) { // 商品名称包含所有非空关键字，能够映射
                return mappingDict.getMappingValue();
            }
        }

        // 没有匹配到映射标签
        return brand + "其他";
    }
}