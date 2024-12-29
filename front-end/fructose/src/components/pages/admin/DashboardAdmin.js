import React, {useState} from "react";
import ListCandidatureEnAttenteContrat from "../candidatures/ListCandidatureEnAttenteContrat";
import UserManagementDashboard from "../home/DashboardSections/UserManagementDashboard";
import OffresStagesAdminDashboard from "../home/DashboardSections/OffresStagesAdminDashboard";
import CVAdminDashboard from "../home/DashboardSections/CVAdminDashboard";
import ViewContrats from "../contrat/ViewContrats";

const DashboardAdmin = () => {

    const [needFetchingContracts, setNeedFetchingContracts] = useState(false);

    return (
        <>
            <UserManagementDashboard></UserManagementDashboard>
            <OffresStagesAdminDashboard></OffresStagesAdminDashboard>
            <CVAdminDashboard></CVAdminDashboard>
            <section>

                <ListCandidatureEnAttenteContrat needFetchingContracts={setNeedFetchingContracts}/>
                <br/>
                <ViewContrats needFetchingContracts={needFetchingContracts}
                              setNeedFetchingContracts={setNeedFetchingContracts}/>
            </section>
        </>
    );
};

export default DashboardAdmin;