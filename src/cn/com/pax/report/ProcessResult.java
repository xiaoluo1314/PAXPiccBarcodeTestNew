package cn.com.pax.report;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ProcessResult {
	public ProcessResult() {
		buTongMap = new LinkedHashMap<String, TiaoMaInfo>();
	}
	
	public void addResult(List<TiaoMaInfo> infos) {
		for(TiaoMaInfo info: infos) {
			buTongMap.put(info.getType() + "/" + info.getDimen() + "/" + info.getCodeName() + "/" + info.getIndName() + "/" + info.getIndicator(), info);
		}
		
	}
	
	public List<String> getKeyList(List<TiaoMaInfo> infos) {
		List<String> keyList = new ArrayList<String>();
		
		for(TiaoMaInfo info: infos) {
			keyList.add(info.getType() + "/" + info.getDimen() + "/" + info.getCodeName() + "/" + info.getIndName() + "/" + info.getIndicator());
		}
		return keyList;
	}
	
	public List<TiaoMaInfo> getTypeList(String typeString) {
		List<TiaoMaInfo> lists = new ArrayList<TiaoMaInfo>();
		for(String keyString2 : buTongMap.keySet()) {
			if(keyString2.contains(typeString)) {
				lists.add(buTongMap.get(keyString2));
			}
		}
		return lists;
	}
	
	public void addResult(String key, TiaoMaInfo value) {
		buTongMap.put(key, value);
	}
	
	public String getResult(String key) {
		return buTongMap.get(key).getResult();
	}
	
	public String getTotalResult(List<String> keys) {
		if(keys.size() < 1) return "-";
		for(String keyString: keys) {
			if(!buTongMap.get(keyString).getResult().equalsIgnoreCase("PASS"))
				return buTongMap.get(keyString).getResult();
		}
		return "PASS";
	}
	
	Map<String, TiaoMaInfo> buTongMap;
}
