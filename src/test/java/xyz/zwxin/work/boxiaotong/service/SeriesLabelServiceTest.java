package xyz.zwxin.work.boxiaotong.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
class SeriesLabelServiceTest {


    @Autowired
    private SeriesLabelService seriesLabelService;

    @Test
    void getSeriesLabel() {
        // 测试用例
        Assert.assertEquals("其他", seriesLabelService.getSeriesLabel("其他", "菠萝啤整箱装 24罐*320ml零酒精果啤果味汽水碳酸饮料夏日饮品"));
        Assert.assertEquals("清爽", seriesLabelService.getSeriesLabel("雪花", "SNOW雪花纯生啤酒8度500ml*12罐匠心营造易拉罐装整箱黄啤酒 500mL*12瓶"));
        Assert.assertEquals("清爽", seriesLabelService.getSeriesLabel("雪花", "雪花啤酒8°清爽啤酒330ml*24听 罐装整箱麦芽酿制 武汉满百包邮"));
        Assert.assertEquals("雪花其他", seriesLabelService.getSeriesLabel("雪花", "雪花啤酒清爽啤酒330ml*24听 罐装整箱麦芽酿制 武汉满百包邮"));
        Assert.assertEquals("花生巧克力牛奶世涛", seriesLabelService.getSeriesLabel("迷失海岸", "进口精酿啤酒迷失海岸花生酱牛奶世涛卡斯四料特浓巧克力组合装"));
        Assert.assertEquals("花生巧克力牛奶世涛", seriesLabelService.getSeriesLabel("迷失海岸", "迷失海岸美国进口精酿啤酒巧克力牛奶花生酱迷雾快艇幽灵浑浊IPA美国原装进口 17种口味可选 355ml 单瓶"));
        Assert.assertEquals("海妖精酿其他", seriesLabelService.getSeriesLabel("海妖精酿", "海妖精酿啤酒瓶装330ml12瓶包邮"));
        Assert.assertEquals("可乐其他", seriesLabelService.getSeriesLabel("可乐", "可口可乐330ml*24听 罐装整箱武汉满百包邮"));

    }
}