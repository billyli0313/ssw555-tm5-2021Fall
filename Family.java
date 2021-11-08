import java.util.Date;
import java.util.List;

public class Family {

	String id;
	String marriageDate;
	String divorceDate;
	String husbandId;
	String husbandName;
	String wifeId;
	String wifeName;
	List<String> childrenId;
	boolean isDivorced;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMarriageDate() {
		return marriageDate == null ? "NA" : marriageDate;
	}
	public void setMarriageDate(String marriageDate) {
		this.marriageDate = marriageDate;
	}
	public boolean isDivorced() {
		return isDivorced;
	}
	public void setDivorced(boolean isDivorced) {
		this.isDivorced = isDivorced;
	}
	public String getDivorceDate() {
		return divorceDate == null ? "NA" : divorceDate;
	}
	public void setDivorceDate(String divorceDate) {
		this.divorceDate = divorceDate;
	}
	public String getHusbandId() {
		return husbandId;
	}
	public void setHusbandId(String husbandId) {
		this.husbandId = husbandId;
	}
	public String getHusbandName() {
		return husbandName;
	}
	public void setHusbandName(String husbandName) {
		this.husbandName = husbandName;
	}
	public String getWifeId() {
		return wifeId;
	}
	public void setWifeId(String wifeId) {
		this.wifeId = wifeId;
	}
	public String getWifeName() {
		return wifeName;
	}
	public void setWifeName(String wifeName) {
		this.wifeName = wifeName;
	}
	public List<String> getChildrenId() {
		return childrenId;
	}

	public String getCIdAsStr() {
		if( childrenId != null && childrenId.size() > 0 ) {
			String value = "{";
			boolean first = true;
			for(String str : childrenId) {
				if(!first) {
					value += ",";
				}
				value += str.replaceAll("@", "'");
				
				first = false;
			}
			value += "}";
			return value;
		} else {
			return "NA";
		}
	}

	public void setCId(List<String> childrenId) {
		this.childrenId = childrenId;
	}
}