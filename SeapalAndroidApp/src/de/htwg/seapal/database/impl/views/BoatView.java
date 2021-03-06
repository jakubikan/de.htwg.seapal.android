package de.htwg.seapal.database.impl.views;

import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;

import java.util.Map;

/**
 * Created by jakub on 2/19/14.
 */
public class BoatView implements Mapper {
    @Override
    public void map(Map<String, Object> stringObjectMap, Emitter emitter) {
        String boat = (String) stringObjectMap.get("boat");
        String owner = (String) stringObjectMap.get("owner");
        if (owner != null && boat != null) {
            emitter.emit(owner.concat(boat), stringObjectMap);

        }

    }
}
