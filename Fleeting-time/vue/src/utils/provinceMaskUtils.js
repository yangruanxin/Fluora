// src/utils/provinceMaskUtils.js

/**
 * 构造一个省份遮罩 Polygon（纯色填充，支持点击事件）
 * 注意：百度地图的 Polygon 不支持图片填充，只能模拟遮罩效果
 */
export function createProvinceMaskOverlay(provinceName, imageUrl, map, BMap) {
  return new Promise((resolve, reject) => {
    const boundary = new BMap.Boundary();
    boundary.get(provinceName, (rs) => {
      if (!rs.boundaries || rs.boundaries.length === 0) {
        reject(`未能获取 ${provinceName} 的边界数据`);
        return;
      }

      const overlays = [];

      rs.boundaries.forEach((boundaryStr) => {
        const points = boundaryStr.split(";").map(p => {
          const [lng, lat] = p.split(",").map(Number);
          return new BMap.Point(lng, lat);
        });

        const polygon = new BMap.Polygon(points, {
          strokeColor: "#fff",
          strokeWeight: 1,
          strokeOpacity: 0.3,
          fillColor: "#000",  // 占位色，模拟遮罩
          fillOpacity: 0.4,
        });

        overlays.push(polygon);
      });

      // 如果有多个多边形，封装成一个自定义 overlay group
      // 否则直接返回单个 polygon
      if (overlays.length === 1) {
        resolve(overlays[0]);
      } else {
        // 构造一个 OverlayGroup 模拟
        const group = {
          overlays,
          addEventListener: (event, handler) => {
            overlays.forEach(o => o.addEventListener(event, handler));
          }
        };
        resolve(group);
      }
    });
  });
}
