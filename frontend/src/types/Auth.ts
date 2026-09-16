export type LoginRequest = {
    username: string;
    password: string;
};

export type LoginResponse = {
    token: string;
};

export type RegisterRequest = {
    username: string;
    password: string;
};