import math

PI = math.pi
earth_radius = 6378245.0
eccentricity_square = 0.00669342162296594323


def bd09togcj02(bd_lng: float, bd_lat: float) -> tuple[float, float]:
    x_pi = (PI * 3000.0) / 180.0
    x = bd_lng - 0.0065
    y = bd_lat - 0.006
    z = math.sqrt(x * x + y * y) - 0.00002 * math.sin(y * x_pi)
    theta = math.atan2(y, x) - 0.000003 * math.cos(x * x_pi)
    gg_lng = z * math.cos(theta)
    gg_lat = z * math.sin(theta)
    return gg_lng, gg_lat


def gcj02tobd09(lng: float, lat: float) -> tuple[float, float]:
    x_pi = (PI * 3000.0) / 180.0
    z = math.sqrt(lng * lng + lat * lat) + 0.00002 * math.sin(lat * x_pi)
    theta = math.atan2(lat, lng) + 0.000003 * math.cos(lng * x_pi)
    bd_lng = z * math.cos(theta) + 0.0065
    bd_lat = z * math.sin(theta) + 0.006
    return bd_lng, bd_lat


def wgs84togcj02(lng: float, lat: float) -> tuple[float, float]:
    if outOfChina(lng, lat):
        return lng, lat
    else:
        dlat = transformlat(lng - 105.0, lat - 35.0)
        dlng = transformlng(lng - 105.0, lat - 35.0)
        radlat = (lat / 180.0) * PI
        magic = math.sin(radlat)
        magic = 1 - eccentricity_square * magic * magic
        sqrtmagic = math.sqrt(magic)
        dlat = (dlat * 180.0) / (
            ((earth_radius * (1 - eccentricity_square)) / (magic * sqrtmagic)) * PI
        )
        dlng = (dlng * 180.0) / ((earth_radius / sqrtmagic) * math.cos(radlat) * PI)
        mglat = lat + dlat
        mglng = lng + dlng
        return mglng, mglat


def gcj02towgs84(lng: float, lat: float) -> tuple[float, float]:
    if outOfChina(lng, lat):
        return lng, lat
    else:
        dlat = transformlat(lng - 105.0, lat - 35.0)
        dlng = transformlng(lng - 105.0, lat - 35.0)
        radlat = (lat / 180.0) * PI
        magic = math.sin(radlat)
        magic = 1 - eccentricity_square * magic * magic
        sqrtmagic = math.sqrt(magic)
        dlat = (dlat * 180.0) / (
            ((earth_radius * (1 - eccentricity_square)) / (magic * sqrtmagic)) * PI
        )
        dlng = (dlng * 180.0) / ((earth_radius / sqrtmagic) * math.cos(radlat) * PI)
        mglat = lat + dlat
        mglng = lng + dlng
        return lng * 2 - mglng, lat * 2 - mglat


def bd09towgs84(lng: float, lat: float) -> tuple[float, float]:
    gcj02 = bd09togcj02(lng, lat)
    return gcj02towgs84(gcj02[0], gcj02[1])


def wgs84tobd09(lng: float, lat: float) -> tuple[float, float]:
    gcj02 = wgs84togcj02(lng, lat)
    result = gcj02tobd09(gcj02[0], gcj02[1])
    return result


def transformlat(lng: float, lat: float) -> float:
    ret = (
        -100.0
        + 2.0 * lng
        + 3.0 * lat
        + 0.2 * lat * lat
        + 0.1 * lng * lat
        + 0.2 * math.sqrt(abs(lng))
    )
    ret += (
        (20.0 * math.sin(6.0 * lng * PI) + 20.0 * math.sin(2.0 * lng * PI)) * 2.0
    ) / 3.0
    ret += ((20.0 * math.sin(lat * PI) + 40.0 * math.sin((lat / 3.0) * PI)) * 2.0) / 3.0
    ret += (
        (160.0 * math.sin((lat / 12.0) * PI) + 320 * math.sin((lat * PI) / 30.0)) * 2.0
    ) / 3.0
    return ret


def transformlng(lng: float, lat: float) -> float:
    ret = (
        300.0
        + lng
        + 2.0 * lat
        + 0.1 * lng * lng
        + 0.1 * lng * lat
        + 0.1 * math.sqrt(abs(lng))
    )
    ret += (
        (20.0 * math.sin(6.0 * lng * PI) + 20.0 * math.sin(2.0 * lng * PI)) * 2.0
    ) / 3.0
    ret += ((20.0 * math.sin(lng * PI) + 40.0 * math.sin((lng / 3.0) * PI)) * 2.0) / 3.0
    ret += (
        (150.0 * math.sin((lng / 12.0) * PI) + 300.0 * math.sin((lng / 30.0) * PI))
        * 2.0
    ) / 3.0
    return ret


def outOfChina(lng: float, lat: float) -> bool:
    return lng < 72.004 or lng > 137.8347 or lat < 0.8293 or lat > 55.8271 or False
