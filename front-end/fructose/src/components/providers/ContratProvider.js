import {AuthContext} from "./AuthProvider";
import React, {useContext} from "react";
import Swal from "sweetalert2";
import {useTranslation} from "react-i18next";

const ContratContext = React.createContext(undefined);

const ContratProvider = ({children}) => {
	const {currentToken} = useContext(AuthContext);
	const {t} = useTranslation();
	
	const fetchContratByCandidatureId = async (id) => {
		try {
			const response = await fetch(`/contrats/candidatures/${id}`, {
				method: "GET",
				headers: {
					'Content-Type': 'application/json',
					'Authorization': currentToken
				}
			});
			
			return await response.json();
		} catch (error) {
			console.error('Error fetching contrat:', error);
			throw error;
		}
	};
	
	const fetchPdf = async (candidatureId) => {
		const response = await fetch(`/contrats/generate/${candidatureId}`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': currentToken
			},
		});
		
		if (!response.ok) {
			const errorText = await response.text();
			console.error('Error response:', errorText);
			throw new Error(`HTTP error! status: ${response.status}`);
		}
		
		const pdfBlob = await response.blob();
		return URL.createObjectURL(pdfBlob);
	};
	
	const fetchPdfByContratId = async (contratId) => {
		const response = await fetch(`/contrats/${contratId}`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': currentToken
			},
		});
		const pdfBlob = await response.blob();
		return URL.createObjectURL(pdfBlob);
	}
	
	const signContract = async (contratId) => {
		const response = await fetch(`/contrats/${contratId}/signer`, {
			method: 'PATCH',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': currentToken
			},
		});
		
		if (!response.ok) {
			const errorText = await response.text();
			console.error('Error response:', errorText);
			throw new Error(`HTTP error! status: ${response.status}`);
		}
	};

	const handleSignerContrat = (contrat, setCurrentCandidature) => {
		console.log(contrat.id);
		try {
			Swal.fire({
				title: t("sign_contract_swal.confirm_sign_title"),
				text: t("sign_contract_swal.confirm_sign_text"),
				icon: 'question',
				showCancelButton: true,
				confirmButtonColor: '#ff006c',
				cancelButtonColor: '#fff',
				confirmButtonText: t("sign_contract_swal.confirm_sign_confirm_button"),
				cancelButtonText: t("sign_contract_swal.confirm_sign_cancel_button")
			}).then((result) => {
				if (result.isConfirmed) {
					signContract(contrat.id).then(r => {
						Swal.fire({
							title: t("sign_contract_swal.success_title"),
							icon: 'success',
							showConfirmButton: false,
							timer: 2000
						});
						setCurrentCandidature(null);
					});
				}
			});
		} catch (error) {
			console.error("Error signing contract: " + error);
		}
	};

	const refuseSignContract = async (contratId) => {
		const response = await fetch(`/contrats/${contratId}/refuser`, {
			method: 'PATCH',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': currentToken
			},
		});

		if (!response.ok) {
			const errorText = await response.text();
			console.error('Error response:', errorText);
			throw new Error(`HTTP error! status: ${response.status}`);
		}
	};

	const handleRefuseSignerContrat = (contrat, setCurrentCandidature) => {
		console.log("Refuse signer contrat: " + contrat.id);
		try {
			Swal.fire({
				title: t("sign_contract_swal.confirm_refuse_title"),
				text: t("sign_contract_swal.confirm_refuse_text"),
				icon: 'question',
				showCancelButton: true,
				confirmButtonColor: '#ff006c',
				cancelButtonColor: '#fff',
				confirmButtonText: t("sign_contract_swal.confirm_refuse_confirm_button"),
				cancelButtonText: t("sign_contract_swal.confirm_refuse_cancel_button")
			}).then((result) => {
				if (result.isConfirmed) {
					refuseSignContract(contrat.id).then(r => {
						Swal.fire({
							title: t("sign_contract_swal.success_refused_title"),
							icon: 'error',
							showConfirmButton: false,
							timer: 2000
						});
						setCurrentCandidature(null);
					});
				}
			});
		} catch (error) {
			console.error("Error refusing contract: " + error);
		}
	}

	const fetchContrats = async () => {
		try {
			const response = await fetch(`/contrats`, {
				method: 'GET',
				headers: {
					'Content-Type': 'application/json',
					'Authorization': currentToken
				},
			});
			if (response.ok) {
				return await response.json();
			} else {
				console.error('Failed to fetch contrats');
			}
		}
		catch (error) {
			console.error('Error fetching contrats:', error);
		}
	}

	
	return (
		<ContratContext.Provider value={{
			fetchContratByCandidatureId,
			fetchPdf,
			fetchPdfByContratId,
			handleSignerContrat,
			handleRefuseSignerContrat,
			fetchContrats
		}}>
			{children}
		</ContratContext.Provider>
	);
};

export {ContratProvider, ContratContext};