package com.pium.riot.commandservice.commands.profilecommand;

import com.pium.riot.api.apiconfig.ApiHenrik;
import com.pium.riot.api.model.ValProfile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ValProfileService {
    public Map<String, ValProfile> profileData = new HashMap<>();
    private final ApiHenrik apiHenrik;

    public ValProfileService(ApiHenrik api) {
        apiHenrik = api;
    }

    public void profilesBuilder(String idMessage) throws IOException {
        ValProfile profile = apiHenrik.getValProfile();
        if (profile != null && !profile.getCurrentRank().equals("Unrated")) {
            profileData.put(idMessage, profile);
        }
    }
}
