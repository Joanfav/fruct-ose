import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import ManageOffresStage from '../ManageOffresStage.js';
import { AuthContext } from '../../../providers/AuthProvider';
import { OffreStageContext } from '../../../providers/OffreStageProvider';
import { BrowserRouter } from 'react-router-dom';

// Mock components and functions
jest.mock('@mdi/react', () => ({ path, size, className }) => (
    <svg path={path} size={size} className={className}></svg>
));

const mockFetchOffresStage = jest.fn();
const mockFetchOffreStage = jest.fn();

describe('ManageOffresStage', () => {
    const renderComponent = (currentUser = null, offreStages = []) => {
        return render(
            <BrowserRouter>
                <AuthContext.Provider value={{ currentUser }}>
                    <OffreStageContext.Provider value={{ fetchOffresStage: mockFetchOffresStage, fetchOffreStage: mockFetchOffreStage }}>
                        <ManageOffresStage />
                    </OffreStageContext.Provider>
                </AuthContext.Provider>
            </BrowserRouter>
        );
    };

    it('renders correctly when there are no offers', async () => {
        mockFetchOffresStage.mockResolvedValue([]);

        renderComponent({ role: 'EMPLOYEUR' });

        await waitFor(() => {
            expect(screen.getByText(/no_offre_stages/i)).toBeInTheDocument();
        });
    });



    it('renders the list of offers when available', async () => {
        // Given
        const mockOffers = [
            { id: 1, nom: 'Offer 1', isApproved: false, isRefused: false },
            { id: 2, nom: 'Offer 2', isApproved: true, isRefused: false },
        ];
        mockFetchOffresStage.mockResolvedValue(mockOffers);

        // When
        renderComponent({ role: 'EMPLOYEUR' });

        // Then
        await waitFor(() => {
            expect(screen.getByText('Offer 1')).toBeInTheDocument();
            expect(screen.getByText('Offer 2')).toBeInTheDocument();
        });
    });

    it('fetches details of a selected offer', async () => {
        // Given
        const mockOffer = { id: 1, nom: 'Offer 1', isApproved: false, isRefused: false };
        mockFetchOffresStage.mockResolvedValue([mockOffer]);
        mockFetchOffreStage.mockResolvedValue(mockOffer);

        // When
        renderComponent({ role: 'EMPLOYEUR' });

        // Wait for the element to be rendered
        await waitFor(() => screen.getByText('Offer 1'));

        fireEvent.click(screen.getByText('Offer 1'));

        // Then
        await waitFor(() => {
            expect(mockFetchOffreStage).toHaveBeenCalledWith(1);
        });
    });


    it('displays the status of the offer', async () => {
        // Given
        const mockOffers = [
            { id: 1, nom: 'Offer 1', isApproved: false, isRefused: false },
            { id: 2, nom: 'Offer 2', isApproved: true, isRefused: false },
            { id: 3, nom: 'Offer 3', isApproved: false, isRefused: true },
        ];
        mockFetchOffresStage.mockResolvedValue(mockOffers);

        // When
        renderComponent({ role: 'EMPLOYEUR' });

        // Then
        await waitFor(() => {
            expect(screen.getByText(/pending/i)).toBeInTheDocument();
            expect(screen.getByText(/approved/i)).toBeInTheDocument();
            expect(screen.getByText(/rejected/i)).toBeInTheDocument();
        });

    });

    it('does not display offers if the user is not an EMPLOYEUR', async () => {
        // Given
        mockFetchOffresStage.mockResolvedValue([]);

        // When
        renderComponent({ role: 'STAGIAIRE' });

        // Then
        await waitFor(() => {
            expect(screen.queryByText('Offer 1')).toBeNull();
        });
    });
});
