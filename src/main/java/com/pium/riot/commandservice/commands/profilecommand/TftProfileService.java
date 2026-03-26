package com.pium.riot.commandservice.commands.profilecommand;

import com.pium.riot.api.apiconfig.ApiRiot;
import com.pium.riot.api.model.TftProfile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TftProfileService {
    public Map<String, TftProfile> profileData = new HashMap<>();
    private final ApiRiot apiRiot;

    public TftProfileService(ApiRiot ap) {
        apiRiot = ap;
    }

    public void profilesBuilder(String idMessage) throws IOException {
        TftProfile profile = apiRiot.getTftProfile();
        profileData.put(idMessage, profile);
    }
}
