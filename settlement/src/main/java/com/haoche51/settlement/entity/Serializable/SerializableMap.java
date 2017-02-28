package com.haoche51.settlement.entity.Serializable;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by xuhaibo on 16/2/29.
 */
public class SerializableMap  implements Serializable{

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public Map<String, Object> getMap() {

        return map;
    }

    private Map<String,Object> map;




}
