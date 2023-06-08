package xyz.zwxin.work.boxiaotong.service;

import xyz.zwxin.work.boxiaotong.pojo.Product;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author zhuwx
 * @description 针对表【product】的数据库操作Service
 * @createDate 2023-06-07 13:03:06
 */
public interface ProductService extends IService<Product> {
    public void updateProductSeries();


}
