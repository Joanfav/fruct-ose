import {Link, useNavigate} from "react-router-dom";
import Icon from "@mdi/react";
import {
    mdiAccountMultipleRemoveOutline,
    mdiAccountOutline,
    mdiAccountSchoolOutline, mdiAccountTieOutline,
    mdiBriefcaseRemoveOutline,
    mdiChevronRight, mdiFileQuestionOutline, mdiHumanMaleBoard
} from "@mdi/js";
import React, {useContext, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {AuthContext} from "../../../providers/AuthProvider";
import {AdminContext} from "../../../providers/AdminProvider";

const UserManagementDashboard = () => {
    const {t} = useTranslation();
    const {currentUser, isUserInit} = useContext(AuthContext);
    const navigate = useNavigate();

    const {GetUnapprovedUsers} = useContext(AdminContext);
    const [users, setUsers] = useState(null);

    const itemsPerPage= 6


    useEffect(() => {
        if (isUserInit) {
            (async function () {
                try {
                    const response = await GetUnapprovedUsers();
                    const data = await response.text();
                    setUsers(JSON.parse(data).slice(0,6));
                } catch (error) {
                    console.error("Erreur lors de la récupération des Utilisateurs:", error);
                }
            })();
        }
    }, [isUserInit, GetUnapprovedUsers]);

    const handleUserClick = (user) => {
        navigate(`/dashboard/admin/manage-users?user=${user.id}`);
    };

    const goToUserManagementPage = () => {
        navigate("/dashboard/admin/manage-users");
    };

    if (currentUser != null) {
        if (currentUser.role === "ADMIN") {
            return (
                <>
                    <section>
                        <div className="toolbar-items">
                            <div className={"toolbar-items"}>
                                <h4 className="m-0">{t("manage_users_page.not_approved_users")}</h4>
                            </div>
                        </div>
                        <br/>
                        <div className="menu-list">
                            {
                                (!users || users.length === 0)?
                                        <div className="menu-list-item menu-list-empty-list-placeholder">
                                            {(!users)?
                                                <div className="loader"></div>
                                                :
                                                <div className="no-items-display">
                                                    <Icon path={mdiAccountMultipleRemoveOutline} size={1.5} />
                                                    <h6>{t("manage_users_page.no_users_to_approve")}</h6>
                                                    <p className="text-dark text-mini">{t("manage_users_page.when_created")}</p>
                                                </div>}
                                        </div>
                                    :
                                    <>
                                        {users.map((user, index) => (
                                            <div className="menu-list-item" key={index} onClick={() => handleUserClick(user)}>
                                                {
                                                    (user.role === "ETUDIANT") ?
                                                        <Icon path={mdiAccountSchoolOutline} size={1}/>
                                                        :
                                                        (user.role === "EMPLOYEUR") ?
                                                            <Icon path={mdiAccountTieOutline} size={1}/>
                                                            :
                                                            (user.role === "PROFESSEUR") ?
                                                                <Icon path={mdiHumanMaleBoard} size={1}/>
                                                                :
                                                                <Icon path={mdiAccountOutline} size={1}/>
                                                }
                                                <div>
                                                    <h6 className="m-0">{user.fullName}</h6>
                                                    <p className="m-0 text-dark">{user.email}</p>
                                                </div>
                                                <div className="toolbar-spacer"></div>
                                            </div>
                                        ))}

                                        {
                                            (users.length < itemsPerPage)
                                                ?
                                                Array.from({length: itemsPerPage - users.length}, (_, i) => (
                                                    <div key={i} className="menu-list-item menu-list-item-placeholder">
                                                    </div>
                                                ))
                                                :
                                                null
                                        }
                                    </>
                            }

                            <div className="menu-list-item menu-list-view-more " onClick={()=>{goToUserManagementPage()}}>
                                <div className="toolbar-spacer"></div>
                                <p className="m-0">{t("dashboard_home_page.see_all")}</p>
                                <Icon path={mdiChevronRight} size={1}/>
                            </div>
                        </div>
                    </section>
                </>

            )
        }
    }
}

export default UserManagementDashboard