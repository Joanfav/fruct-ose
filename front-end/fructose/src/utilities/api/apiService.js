export const isMatriculeTaken = async (matricule) => {
	try {
		const response = await fetch(`/check-matricule?matricule=${encodeURIComponent(matricule)}`);
		const data = await response.json();
		return data.matriculeTaken;
	} catch (error) {
		console.error(error);
		return false;
	}
};

export const isEmailTaken = async (email) => {
	try {
		const response = await fetch(`/check-email?email=${encodeURIComponent(email)}`);
		const data = await response.json();
		return data.emailTaken;
	} catch (error) {
		console.error(error);
		return false;
	}
};

export const getDepartement = async (departementName) => {
	try {
		const response = await fetch(`/check-departement?departementName=${encodeURIComponent(departementName)}`);
		const data = await response.json();
		return data.departement;
	} catch (error) {
		console.error(error);
		return null
	}
}

export const getEmployeur = async (id, token) => {
	try {
		console.log(id, token)
		const response = await fetch(`/employeur/${id}`, {
			headers: {
				"Content-Type": "application/json",
				"Authorization": `${token}`,
			},
		});
		console.log(response);
		const data = await response.json();
		return data.owner;
	} catch (error) {
		console.error("une erreur est survenu " + error);
		return null
	}
}