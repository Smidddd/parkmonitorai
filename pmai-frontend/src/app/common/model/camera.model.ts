import { Area } from "./area.model";

export interface Camera {
    id: number,
    name: string,
    lattitude: number,
    longitude: number,
    source: string,
    areaId: Area,
    status: number
}