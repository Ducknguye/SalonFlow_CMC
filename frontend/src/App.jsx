import { useEffect, useState } from "react";
import './App.css'
import api from "./api/api";

function App() {

    const [message, setMessage] = useState("");

    useEffect(() => {

        api.get("/api/test")
            .then((response) => {
                setMessage(response.data);
            })
            .catch((error) => {
                console.error(error);
            });

    }, []);

    return (
        <>
            <h1>Welcome to my Salon</h1>
            <p>{message}</p>
        </>
    );
}

export default App;
