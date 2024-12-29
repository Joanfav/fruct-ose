import React from "react";
import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from "@testing-library/react";
import {AuthContext} from "../../../providers/AuthProvider";
import {OffreStageContext} from "../../../providers/OffreStageProvider";
import {BrowserRouter as Router} from "react-router-dom";
import ModifierOffreStage from "../ModifierOffreStage";

// Mocking react-i18next
jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key) => key,
        i18n: { changeLanguage: jest.fn() }
    }),
    I18nextProvider: ({ children }) => <>{children}</>
}));

const mockAuthContext = {
    currentToken: 'mockToken',
    currentUser: { role: 'EMPLOYEUR' }
};

const mockFetchOffreStage = jest.fn();

afterEach(() => {
    jest.resetAllMocks();
});

describe('ModifierOffreStage Component', () => {
    test('Renders without errors', async () => {
        const mockOffer = JSON.parse("{\"id\":32,\"nom\":\"aaaaaa\",\"poste\":\"Scrum Master\",\"description\":\"dsdsdsdsadas\",\"compagnie\":\"Alex Inc.\",\"departementDTO\":{\"id\":17,\"nom\":\"techniques_informatique\"},\"tauxHoraire\":0.0,\"typeEmploi\":\"presentiel\",\"adresse\":\"6666 Rue du diable H6A 6N6\",\"modaliteTravail\":\"temps_plein\",\"dateDebut\":\"2024-11-29\",\"dateFin\":\"2024-12-07\",\"nombreHeuresSemaine\":1,\"nombrePostes\":7,\"dateLimiteCandidature\":\"2024-11-28\",\"ownerDTO\":{\"id\":13,\"fullName\":\"Yves Guillemot\",\"email\":\"ubisoft@gmail.com\",\"password\":\"$2a$10$cGX2U0jw1xzHFOys9c.Hl.BXgm3XUAjFT9xLlEvuFN1KP3S1sH4xC\",\"matricule\":null,\"role\":\"EMPLOYEUR\",\"departementDTO\":{\"id\":17,\"nom\":\"techniques_informatique\"},\"companyName\":\"Ubisoft Incorporé\",\"isApproved\":true},\"isApproved\":false,\"isRefused\":false,\"commentaireRefus\":\"Commentaire par défaut\"}")
        mockFetchOffreStage.mockResolvedValue(mockOffer);
        render(
            <AuthContext.Provider value={mockAuthContext}>
                <OffreStageContext.Provider value={{fetchOffreStage: mockFetchOffreStage}}>
                    <Router>
                        <ModifierOffreStage/>
                    </Router>
                </OffreStageContext.Provider>
            </AuthContext.Provider>
        );
        await waitFor(() => {
            expect(screen.getByText(/modifier_offre_stage_page.title/i)).toBeInTheDocument();
        });
    });

    test('Returns 404 error when not found', async () => {
        // Given
        const mockOffer = null
        mockFetchOffreStage.mockResolvedValue(mockOffer);
        // When
        render(
            <AuthContext.Provider value={mockAuthContext}>
                <OffreStageContext.Provider value={{fetchOffreStage: mockFetchOffreStage}}>
                    <Router>
                        <ModifierOffreStage/>
                    </Router>
                </OffreStageContext.Provider>
            </AuthContext.Provider>
        );
        // Then

        await waitFor(() => {
            expect(screen.getByText(/Erreur 404/i)).toBeInTheDocument();
        });
    });
})