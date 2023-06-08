package xyz.zwxin.work.boxiaotong.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.zwxin.work.boxiaotong.pojo.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author zhuwx
 * @description 针对表【product】的数据库操作Mapper
 * @createDate 2023-06-07 13:03:06
 * @Entity xyz.zwxin.work.boxiaotong.pojo.Product
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

}




