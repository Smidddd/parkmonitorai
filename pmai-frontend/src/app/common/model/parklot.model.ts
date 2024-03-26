import { Camera } from "./camera.model"

export interface Parklot {
    geometry: Point[],
    latitude: number,
    longitude: number,
    camera: Camera
}
export interface Point {
    x: number,
    y: number
}