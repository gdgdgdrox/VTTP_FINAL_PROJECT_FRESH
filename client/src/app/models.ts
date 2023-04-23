
export interface Deal{
    uuid: string,
    name: string,
    description: string,
    venue: string,
    latitude: number,
    longitude: number
    validStartDate: Date,
    validEndDate: Date,
    tnc: string,
    imageURL: string,
    websiteURL: string,
    saved?: boolean
}

export interface NewUser{
    firstName: string,
    lastName: string,
    dob: Date,
    email: string;
    password: string;
    receiveUpdate: boolean
}

export interface ExistingUser{
    email: string,
    password: string
}