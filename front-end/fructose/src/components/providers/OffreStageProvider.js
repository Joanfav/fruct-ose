import React, {createContext, useContext, useState} from 'react';
import {AuthContext} from "./AuthProvider";
import {useTranslation} from "react-i18next";
import Swal from "sweetalert2";

const OffreStageContext = createContext(undefined);

const OffreStageProvider = ({children}) => {
	
	const {t} = useTranslation();
	const {currentToken} = useContext(AuthContext);
	const [offreStage, setOffreStage] = useState({
		nom: '',
		poste: '',
		description: '',
		compagnie: '',
		departementDTO: '',
		tauxHoraire: 0.0,
		typeEmploi: '',
		adresse: '',
		modaliteTravail: '',
		dateDebut: new Date(),
		dateFin: new Date(),
		nombreHeuresSemaine: 1,
		nombrePostes: 1,
		dateLimiteCandidature: new Date(),
		isRefused: false,
		isApproved: false,
		commentaireRefus: ''
	});
	const [errors, setErrors] = useState({});
	
	const fetchOffresStage = async () => {
		try {
			const response = await fetch('/get-offre-stage', {
				method: "GET",
				headers: {
					'Content-Type': 'application/json',
					'Authorization': currentToken
				},
			});
			if (response.ok) {
				return await response.json();
			}
			return [];
		} catch (error) {
			return [];
		}
	}
	
	const fetchOffreStage = async (id) => {
		try {
			const response = await fetch(`/get-offre-stage/${id}`, {
				method: "GET",
				headers: {
					'Content-Type': 'application/json',
					'Authorization': currentToken
				},
			});
			if (response.ok) {
				return await response.json();
			}
		} catch (error) {
			console.error("Erreur lors de la récupération de l'offre de stage:", error);
		}
	};
	
	const deleteOffreStage = async (id) => {
		const result = await Swal.fire({
			title: t('dashboard_home_page.delete_stage_confirmation'),
			text: t('dashboard_home_page.delete_stage_confirmation_text'),
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#ff006c',
			cancelButtonColor: '#fff',
			confirmButtonText: t('dashboard_home_page.delete_stage_confirm_button_text'),
			cancelButtonText: t('dashboard_home_page.delete_stage_cancel_button_text')
		});
		
		if (result.isConfirmed) {
			return await fetch(`/delete-offre-stage/${id}`, {
				method: "DELETE",
				headers: {
					'Content-Type': 'application/json',
					'Authorization': currentToken
				},
			});
		}
	}
	
	const updateOffreStage = async (updatedData) => {
		const response = await fetch(`/modifier-offre-stage`, {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': currentToken
			},
			body: JSON.stringify(updatedData)
		});
		
		if (response.ok) {
			return response.json();
		} else {
			const contentType = response.headers.get('content-type');
			if (contentType && contentType.includes('application/json')) {
				const errorData = await response.json();
				throw new Error(errorData.message || "An error occurred while updating the offer.");
			} else {
				const errorMessage = await response.text();
				throw new Error(errorMessage);
			}
		}
	};
	
	return (
		<OffreStageContext.Provider value={{
			offreStage,
			setOffreStage,
			errors,
			setErrors,
			fetchOffresStage,
			deleteOffreStage,
			fetchOffreStage,
			updateOffreStage
		}}>
			{children}
		</OffreStageContext.Provider>
	);
};

export {OffreStageProvider, OffreStageContext};