import React, {useContext} from 'react';
import {Navigate} from 'react-router-dom';
import {AuthContext} from "../../components/providers/AuthProvider";

const RoleRoute = ({element: Component, roles}) => {
	const {isSignedIn, currentUser, isUserInit} = useContext(AuthContext);
	
	if (!isUserInit) {
		return (
			<div className="loader-container">
				<div className="loader"></div>
			</div>
		);
	} else {
		if (!isSignedIn() || !currentUser) {
			return <Navigate to="/dashboard"/>;
		}
		return roles.includes(currentUser.role) ? Component : <Navigate to="/dashboard"/>;
	}
};

export {RoleRoute};