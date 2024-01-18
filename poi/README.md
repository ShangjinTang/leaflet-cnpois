# POI

Crawl poi data first, and present data with React & Leaflet.

```bash
# crawl data
poetry run python3 ./01_crawl_raw_pois/crawl_raw_pois.py
# convert to geojson
poetry run python3 ./02_to_geojson_pois/convert_crawled_pois_to_geojson.py
```
