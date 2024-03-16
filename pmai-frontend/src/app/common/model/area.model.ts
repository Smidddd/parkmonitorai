import { User } from "./user.model";

export interface Area {
    id: number,
    name: string,
    userEntities: User[]
}