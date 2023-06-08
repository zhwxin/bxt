package xyz.zwxin.work.boxiaotong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.zwxin.work.boxiaotong.mapper.MappingDictMapper;
import xyz.zwxin.work.boxiaotong.pojo.MappingDict;
import xyz.zwxin.work.boxiaotong.service.SeriesLabelService;

import java.util.List;

@Service
public class SeriesLabelServiceImpl
        implements SeriesLabelService {
    @Autowired
    private MappingDictMapper dictMapper;

    private static final String OTHER = "其他";

    // 根据品牌查询映射词典
    private List<MappingDict> getMappingDictByBrand(String brand) {
        QueryWrapper<MappingDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("brand", brand);
        return dictMapper.selectList(queryWrapper);
    }

    @Override
    public String getSeriesLabel(String brand, String productName) {
        // 先识别品牌，如果品牌值是其他，那么系列就是其他
        if (OTHER.equals(brand)) {
            return OTHER;
        }

        // 如果品牌有值，那么根据商品名称去匹配第一关键字，匹配到了就打上映射值的标签
        List<MappingDict> firstDictList = getMappingDictByBrand(brand);
        for (MappingDict dict : firstDictList) {
            if (productName.contains(dict.getFirstKeyword())) {
                return dict.getMappingValue();
            }
        }

        // 如果第一关键字匹配不到，就要求匹配2，3，4关键字（2 3 4是 and关系。需要都匹配到才可以）
        List<MappingDict> dictList = getMappingDictByBrand(brand);
        for (MappingDict dict : dictList) {
            //2，3，4关键字都不存在或为空
            if ((dict.getSecondKeyword() == null || "".equals(dict.getSecondKeyword()))
                    && (dict.getThirdKeyword() == null || "".equals(dict.getThirdKeyword()))
                    && (dict.getFourthKeyword() == null || "".equals(dict.getFourthKeyword()))) {
                continue;
            }
            if (dict.getSecondKeyword() != null
                    && !"".equals(dict.getSecondKeyword())
                    && !productName.contains(dict.getSecondKeyword())) {
                continue;
            }
            if (dict.getThirdKeyword() != null
                    && !"".equals(dict.getThirdKeyword())
                    && !productName.contains(dict.getThirdKeyword())) {
                continue;
            }
            if (dict.getFourthKeyword() != null
                    && !"".equals(dict.getFourthKeyword())
                    && !productName.contains(dict.getFourthKeyword())) {
                continue;
            }
            return dict.getMappingValue();
        }

        // 如果最终品牌有值，系列没打上标签，那么系列值就等于 品牌+其他
        return brand + OTHER;
    }
}
