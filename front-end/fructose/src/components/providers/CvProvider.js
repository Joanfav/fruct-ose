import React, {createContext, useContext} from 'react';
import {AuthContext} from "./AuthProvider";

const CvContext = createContext(undefined);

const CvProvider = ({children}) => {
	
	const {currentToken} = useContext(AuthContext)
	
	const UploadCv = async (file) => {
		let formData = new FormData()
		formData.append("file", file)
		
		return fetch('/deposer-cv', {
			method: 'POST',
			headers: {
				'Authorization': currentToken
			},
			body: formData
		})
	};
	
	const GetCvs = async () => {
		return fetch('/cvs', {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': currentToken
			},
		})
	};
	
	const getCvContenuById = async (id) => {
		return fetch(`/cvContenu/${id}`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': currentToken
			},
		});
	};
	
	const getCvById = async (id) => {
		const response = await fetch(`/cvs/${id}`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': currentToken
			},
		});
		if (!response.ok) {
			throw new Error('Network response was not ok');
		}
		return response.json();
	};
	
	const GetAllCvs = async () => {
		return fetch(`/allCvs`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': currentToken
			},
		});
	};
	
	const DeleteCv = async (id) => {
		return fetch(`/cvs/${id}`, {
			method: 'DELETE',
			headers: {
				'Authorization': currentToken,
			},
		});
	};
	
	return (
		<CvContext.Provider value={{UploadCv, GetCvs, getCvById, DeleteCv, GetAllCvs, getCvContenuById}}>
			{children}
		</CvContext.Provider>
	);
};

export {CvProvider, CvContext};