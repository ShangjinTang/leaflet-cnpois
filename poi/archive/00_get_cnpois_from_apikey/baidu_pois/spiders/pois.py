#!/usr/bin/env python3

import csv
import json
import os

import scrapy

QUERIES_AND_TAGS = {"高铁站": "高铁站", "地铁站": "地铁站", "医院": "医院"}
CITYCODE_TXT = "assets/BaiduMap_cityCode_1102.txt"
CRAWLED_DATA_DIR = "output"


class PoisSpider(scrapy.Spider):
    name = "pois"
    allowed_domains = ["api.map.baidu.com"]
    start_urls = ["https://api.map.baidu.com"]

    def __init__(self, *args, **kwargs):
        super(PoisSpider, self).__init__(*args, **kwargs)
        self.access_key = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
        self.regions = self.load_regions()

    def load_regions(self):
        regions = []
        with open(CITYCODE_TXT, "r") as file:
            reader = csv.reader(file)
            next(reader)
            for row in reader:
                area_id, name = row
                regions.append({"area_id": area_id, "name": name})
        return regions

    def start_requests(self):
        for query, tag in QUERIES_AND_TAGS.items():
            for region in self.regions:
                region_name = region["name"]
                if tag:
                    url = f"https://api.map.baidu.com/place/v2/search?query={query}&tag={tag}&region={region_name}&output=json&ak={self.access_key}"
                else:
                    url = f"https://api.map.baidu.com/place/v2/search?query={query}&region={region_name}&output=json&ak={self.access_key}"
                yield scrapy.Request(
                    url,
                    self.parse,
                    meta={"query": query, "tag": tag, "region": region_name},
                )

    def parse(self, response):
        data = json.loads(response.text)
        print(response.text)
        query = response.meta["query"]
        # tag = response.meta["tag"]
        region = response.meta["region"]

        directory_name = query
        file_name = f"{region}.json"

        os.makedirs(os.path.join(CRAWLED_DATA_DIR, directory_name), exist_ok=True)

        filepath = os.path.join(CRAWLED_DATA_DIR, directory_name, file_name)

        results = data.get("results", [])

        status = data.get("status")
        message = data.get("message")

        if status == 0 and message == "ok":
            with open(filepath, "w") as f:
                json.dump(results, f, ensure_ascii=False)

            self.log(f"Saved file {filepath}")
        else:
            self.log(f"Error: Status={status}, Message={message}")
