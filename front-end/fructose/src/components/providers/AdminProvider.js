import React, {createContext, useContext} from 'react';
import {AuthContext} from "./AuthProvider";

const AdminContext = createContext(undefined);

const AdminProvider = ({children}) => {
	
	const {currentToken} = useContext(AuthContext)
	
	const GetUnapprovedUsers = async () => {
		return fetch('/non-approved-users', {
			method: 'GET',
			headers: {
				'Authorization': currentToken
			},
		})
	};
	
	const ApproveUser = async (id) => {
		return fetch('/approve-user/' + id, {
			method: 'PUT',
			headers: {
				'Authorization': currentToken
			},
		})
	};
	
	const RejectUser = async (id) => {
		return fetch('/reject-user/' + id, {
			method: 'DELETE',
			headers: {
				'Authorization': currentToken
			},
		})
	};
	
	return (
		<AdminContext.Provider value={{GetUnapprovedUsers, ApproveUser, RejectUser}}>
			{children}
		</AdminContext.Provider>
	);
};

export {AdminProvider, AdminContext};