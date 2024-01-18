import "leaflet/dist/leaflet.css";
import React, { useEffect, useState } from "react";
import { MapContainer, TileLayer, useMap, Marker, Popup } from "react-leaflet";
import { Cascader, ConfigProvider, Button } from "antd";
import poiData from "./poi.json";
import marker from "./assets/marker.svg";
import L from "leaflet";
import { bd09towgs84 } from "./js/coordinate-conversion.js";

function convertBd09ToWsg84(coordinates) {
  const [lng, lat] = coordinates.split(",").map(parseFloat);
  const [convertedLng, convertedLat] = bd09towgs84(lng, lat);
  return [convertedLat, convertedLng];
}

const transformData = (data) => {
  const provinces = {};

  data.forEach((location) => {
    const { province, city, name } = location;

    if (!provinces[province]) {
      provinces[province] = { value: province, label: province, children: [] };
    }

    const provinceObj = provinces[province];
    const cityObj = provinceObj.children.find((c) => c.value === city);

    if (!cityObj) {
      provinceObj.children.push({ value: city, label: city, children: [{ value: name, label: name }] });
    } else {
      cityObj.children.push({ value: name, label: name });
    }
  });

  return Object.values(provinces);
};

const options = transformData(poiData);

const App = () => {
  const [selectedLocation, setSelectedLocation] = useState(null);
  const [selectedCity, setSelectedCity] = useState(null);

  const handleLocationChange = (value) => {
    const locationData = poiData.find((location) => location.name === value[2]);
    setSelectedLocation(locationData);
  };

  const ChangeView = ({ center }) => {
    const map = useMap();
    map.setView(center, 13, { animate: true });
    return null;
  };

  const markerIcon = L.icon({
    iconUrl: marker,
    iconSize: [32, 32],
    shadowUrl: null,
    shadowSize: null,
    shadowAnchor: null,
  });

  return (
    <div>
      <h1>POI 地图</h1>
      <Cascader options={options} onChange={handleLocationChange} placeholder="选择位置" />
      <MapContainer center={[31.2304, 121.4737]} zoom={14}>
        <ChangeView
          center={selectedLocation ? convertBd09ToWsg84(selectedLocation.coordinates) : [31.2304, 121.4737]}
        />
        <TileLayer
          attribution="&copy; <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors"
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {selectedLocation && (
          <Marker position={convertBd09ToWsg84(selectedLocation.coordinates)} icon={markerIcon}>
            <Popup>
              <div>
                <h2>{selectedLocation.name}</h2>
                <h3>
                  {selectedLocation.province} {selectedLocation.city}
                </h3>
                <p>{selectedLocation.address}</p>
                <p>
                  百度坐标 (BD-09 坐标系)：
                  <li>经度：{selectedLocation.coordinates.split(",")[0]} </li>
                  <li>纬度：{selectedLocation.coordinates.split(",")[1]} </li>
                </p>
                <p>
                  全球坐标 (WGS-84 坐标系)：
                  <li>经度：{convertBd09ToWsg84(selectedLocation.coordinates)[1]} </li>
                  <li>纬度：{convertBd09ToWsg84(selectedLocation.coordinates)[0]} </li>
                </p>
              </div>
            </Popup>
          </Marker>
        )}
      </MapContainer>
    </div>
  );
};

export default App;
