import React, {createContext, useContext, useState} from 'react';
import {AuthContext} from "./AuthProvider";

const CandidatureContext = createContext(undefined);

const CandidatureProvider = ({children}) => {
	
	const {currentToken} = useContext(AuthContext);
	const [candidatures, setCandidatures] = useState([]);
	
	const ApplyOffreStage = async (id, cvId) => {
		return await fetch(`/candidatures/postuler`, {
			method: "POST",
			headers: {
				'Content-Type': 'application/json',
				'Authorization': currentToken
			},
			body: JSON.stringify({
				offreStageId: id,
				cvId: cvId
			})
		});
	};
	
	const fetchCandidaturesById = async (id) => {
		try {
			const response = await fetch(`/candidatures/etudiant/${id}`, {
				method: 'GET',
				headers: {
					'Content-Type': 'application/json',
					'Authorization': `Bearer ${currentToken}`,
				},
			});
			if (response.ok) {
				const data = await response.json();
				setCandidatures(data);
			} else {
				console.error('Échec de la récupération des candidatures');
			}
		} catch (error) {
			console.error('Erreur lors de la récupération des candidatures :', error);
		}
	};
	
	const fetchCandidatureByEtatAccepteApresEntrevue = async () => {
		try {
			const response = await fetch(`/candidatures/accepteApresEntrevue`, {
				method: 'GET',
				headers: {
					'Content-Type': 'application/json',
					"Authorization": currentToken
				},
			});
			if (response.ok) {
				const data = await response.json();
				setCandidatures(data);
				return data;
			} else {
				throw new Error('Failed to fetch candidatures');
			}
		} catch (error) {
			console.error("Erreur " + error);
		}
	}
	const fetchStagiaireByOwner = async () => {
		try {
			const response = await fetch('/candidatures/stagiaireByOwner', {
				method: 'GET',
				headers: {
					'Content-Type': 'application/json',
					'Authorization': currentToken,
				}
			});

			if (response.ok) {
				const data = await response.json();
				console.log('Candidatures:', data);
				return data;
			} else {
				const errorText = await response.text();
				throw new Error(errorText);
			}
		} catch (error) {
			console.error('Erreur lors de la récupération des candidatures:', error);
		}
	};
	const fetchCandidatureByEtatContratSigneTous = async () => {
		try {
			const response = await fetch(`/candidatures/contratSigneTous`, {
				method: 'GET',
				headers: {
					'Content-Type': 'application/json',
					"Authorization": currentToken
				},
			});
			if (response.ok) {
				const data = await response.json();
				console.log('Candidatures:', data);
				return data;
			} else {
				throw new Error('Failed to fetch candidatures');
			}
		}
		catch (error) {
			console.error("Erreur " + error);
		}
	}
	return (
		<CandidatureContext.Provider value={{
			candidatures,
			fetchCandidaturesById,
			ApplyOffreStage,
			setCandidatures,
			fetchCandidatureByEtatAccepteApresEntrevue,
			fetchStagiaireByOwner,
			fetchCandidatureByEtatContratSigneTous
		}}>
			{children}
		</CandidatureContext.Provider>
	);
};

export {CandidatureProvider, CandidatureContext};