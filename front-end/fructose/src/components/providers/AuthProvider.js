import React, {createContext, useEffect, useRef, useState} from 'react';
import {useNavigate} from "react-router-dom";

const AuthContext = createContext(undefined);

const AuthProvider = ({children}) => {
	
	const [currentUser, setCurrentUser] = useState(null);
	const navigate = useNavigate();
	const [currentToken, setCurrentToken] = useState(null)
	const [isUserInit, setIsUserInit] = useState(false)

	const isInitialized = useRef(false);

	useEffect(() => {
		if (!isInitialized.current) {
			const token = localStorage.getItem("FOSE_AUTH");
			if (token) {
				SetUserByToken(token);
			}
			isInitialized.current = true;
		}
	}, []);
	
	const isSignedIn = () => {
		return localStorage.getItem("FOSE_AUTH") != null;
	}
	
	const SetUserByToken = (token) => {
		fetch('/infos-utilisateur', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': token
			}
		})
			.then(response => response.json())
			.then(data => {
				setCurrentUser(data)
				setIsUserInit(true)
				setCurrentToken(token)
			})
			.catch(() => {
				localStorage.removeItem("FOSE_AUTH");
				setIsUserInit(false)
				setCurrentToken(null)
			});
	};
	
	const SignInUser = async (email, password) => {
		return await fetch('/connexion', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({email, password})
		})
			.then(async response => {
				if (response.ok !== true) {
					throw new Error(await response.text());
				}
				return response
			})
			.then(response => response.text())
			.then(response => {
				localStorage.setItem("FOSE_AUTH", response.substring(7));
				SetUserByToken(response.substring(7));
				navigate("/dashboard")
			})
	};
	
	const SignOutUser = () => {
		setCurrentUser(null)
		setCurrentToken(null)
		localStorage.removeItem("FOSE_AUTH")
		navigate("/")
	};
	
	return (
		<AuthContext.Provider value={{
			isUserInit,
			currentUser,
			currentToken,
			SignInUser,
			SignOutUser,
			isSignedIn
		}}>
			{children}
		</AuthContext.Provider>
	);
};

export {AuthProvider, AuthContext};