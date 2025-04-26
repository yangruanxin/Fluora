package org.whu.fleetingtime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@CrossOrigin
@RestController
@RequestMapping("/api/map")
public class MapController {

    @Value("${baidu.map.ak}")
    private String baiduAk;

    @Autowired
    private RestTemplate restTemplate;

    private static final String BAIDU_REVERSE_GEOCODE_URL = "https://api.map.baidu.com/reverse_geocoding/v3/";

    @GetMapping("/reverse-geocode")
    public String reverseGeocode(
            @RequestParam("lat") String latitude,
            @RequestParam("lng") String longitude
    ) {
        // request url
        String url = BAIDU_REVERSE_GEOCODE_URL +
                "?ak=" + baiduAk +
                "&extensions_poi=1"+
                "&entire_poi=1"+
                "sort_strategy=distance" +
                "&output=json" +
                "&coordtype=bd09ll&coordtype=wgs84ll"+
                "&location=" + latitude + "," + longitude;
        return restTemplate.getForObject(url, String.class);
    }
}
