package com.github.hongkaiwen.login2f.store;


import com.github.hongkaiwen.login2f.util.GoogleUtil;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class BindInfoStore {

    public static ConcurrentHashMap<String, String> seedStore = new ConcurrentHashMap<>();

    public String bind (String loginName){
        String seed = GoogleUtil.getRandomSecretKey();

        seedStore.put(loginName, seed);
        return seed;
    }

    public String get(String loginName){
        return seedStore.get(loginName);
    }


}
