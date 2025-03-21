import React from 'react';
import { render, screen } from '@testing-library/react';
import { NavigationProvider, useNavigation } from '../NavigationContext';
import { useNavigate } from 'react-router-dom';

jest.mock('react-router-dom', () => ({
  useNavigate: jest.fn(),
}));

const TestComponent = ({ onTest }) => {
  const navigate = useNavigation();
  onTest && onTest(navigate);
  return <div data-testid="nav-result">{navigate ? 'present' : 'absent'}</div>;
};

describe('NavigationContext', () => {
  it('provides a navigate function to consumers via useNavigation', () => {
    const fakeNavigate = jest.fn();
    useNavigate.mockReturnValue(fakeNavigate);
    
    render(
      <NavigationProvider>
        <TestComponent />
      </NavigationProvider>
    );
    
    expect(screen.getByTestId('nav-result').textContent).toBe('present');
  });

  it('returns the same navigate function as returned by useNavigate', () => {
    const fakeNavigate = jest.fn();
    useNavigate.mockReturnValue(fakeNavigate);
    
    let obtainedNavigate = null;
    const captureNavigate = (nav) => { obtainedNavigate = nav; };
    
    render(
      <NavigationProvider>
        <TestComponent onTest={captureNavigate} />
      </NavigationProvider>
    );
    
    expect(obtainedNavigate).toBe(fakeNavigate);
  });
});
