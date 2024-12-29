import React, {useContext} from 'react';
import {Navigate} from 'react-router-dom';
import {AuthContext} from "../../components/providers/AuthProvider";

const PrivateRoute = ({element: Component}) => {
	const {isSignedIn} = useContext(AuthContext);
	return isSignedIn() ? Component : <Navigate to="/connexion"/>;
};

export {PrivateRoute};