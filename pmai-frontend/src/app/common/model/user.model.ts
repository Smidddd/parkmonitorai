export interface User {
    id: number,
    email: string,
    password: string,
    role: Role
}
export interface LoginRequest{
    userEmail: string,
    userPassword: string
}
export enum Role{
    ADMIN='ADMIN',
    USER='USER'
}