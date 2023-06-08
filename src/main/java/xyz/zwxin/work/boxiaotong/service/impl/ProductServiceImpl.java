package xyz.zwxin.work.boxiaotong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.zwxin.work.boxiaotong.mapper.ProductMapper;
import xyz.zwxin.work.boxiaotong.pojo.Product;
import xyz.zwxin.work.boxiaotong.service.ProductService;
import xyz.zwxin.work.boxiaotong.service.SeriesLabelService;

import java.util.List;


/**
 * @author zhuwx
 * @description 针对表【product】的数据库操作Service实现
 * @createDate 2023-06-08 13:03:06
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
        implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private SeriesLabelService seriesLabelService;

    // 每一批次的商品数据条数
    private static final int BATCH_SIZE = 1000;


    /**
     * 批量更新商品系列标签
     */
    @Override
    public void updateProductSeries() {
        // 查询数据库中所有商品的数量
        int total = productMapper.selectCount(null);
        // 计算需要分批处理的批次数
        int batchCount = (total + BATCH_SIZE - 1) / BATCH_SIZE;

        // 遍历每一批次的商品数据
        for (int i = 0; i < batchCount; i++) {
            // 计算当前批次的起始位置
            int offset = i * BATCH_SIZE;
            // 查询该批次的商品数据
            List<Product> productList = productMapper.selectList(new QueryWrapper<Product>().last("limit " + offset + "," + BATCH_SIZE));
            // 遍历该批次的每一条商品记录
            for (Product product : productList) {
                // 调用 seriesLabelService 的 getSeriesLabel 方法，获取该商品的系列标签
                String series = seriesLabelService.getSeriesLabel(product.getBrand(), product.getProductName());
                // 将获取到的系列标签更新到该商品记录中
                product.setSeries(series);
            }
            for (Product product : productList) {
                // 将该批次的商品记录批量更新到数据库中
                productMapper.updateById(product);
            }
        }
    }

}




