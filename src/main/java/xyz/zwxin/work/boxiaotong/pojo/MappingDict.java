package xyz.zwxin.work.boxiaotong.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName mapping_dict
 */
@TableName(value ="mapping_dict")
@Data
public class MappingDict implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "brand")
    private String brand;

    /**
     * 
     */
    @TableField(value = "first_keyword")
    private String firstKeyword;

    /**
     * 
     */
    @TableField(value = "second_keyword")
    private String secondKeyword;

    /**
     * 
     */
    @TableField(value = "third_keyword")
    private String thirdKeyword;

    /**
     * 
     */
    @TableField(value = "fourth_keyword")
    private String fourthKeyword;

    /**
     * 
     */
    @TableField(value = "mapping_value")
    private String mappingValue;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        MappingDict other = (MappingDict) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getBrand() == null ? other.getBrand() == null : this.getBrand().equals(other.getBrand()))
            && (this.getFirstKeyword() == null ? other.getFirstKeyword() == null : this.getFirstKeyword().equals(other.getFirstKeyword()))
            && (this.getSecondKeyword() == null ? other.getSecondKeyword() == null : this.getSecondKeyword().equals(other.getSecondKeyword()))
            && (this.getThirdKeyword() == null ? other.getThirdKeyword() == null : this.getThirdKeyword().equals(other.getThirdKeyword()))
            && (this.getFourthKeyword() == null ? other.getFourthKeyword() == null : this.getFourthKeyword().equals(other.getFourthKeyword()))
            && (this.getMappingValue() == null ? other.getMappingValue() == null : this.getMappingValue().equals(other.getMappingValue()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getBrand() == null) ? 0 : getBrand().hashCode());
        result = prime * result + ((getFirstKeyword() == null) ? 0 : getFirstKeyword().hashCode());
        result = prime * result + ((getSecondKeyword() == null) ? 0 : getSecondKeyword().hashCode());
        result = prime * result + ((getThirdKeyword() == null) ? 0 : getThirdKeyword().hashCode());
        result = prime * result + ((getFourthKeyword() == null) ? 0 : getFourthKeyword().hashCode());
        result = prime * result + ((getMappingValue() == null) ? 0 : getMappingValue().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", brand=").append(brand);
        sb.append(", firstKeyword=").append(firstKeyword);
        sb.append(", secondKeyword=").append(secondKeyword);
        sb.append(", thirdKeyword=").append(thirdKeyword);
        sb.append(", fourthKeyword=").append(fourthKeyword);
        sb.append(", mappingValue=").append(mappingValue);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}