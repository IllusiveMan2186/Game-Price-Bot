import React from 'react';
import { render, screen } from '@testing-library/react';
import { NavigationProvider, useNavigation } from '../NavigationContext';
import { useNavigate } from 'react-router-dom';

// Mock useNavigate from react-router-dom.
jest.mock('react-router-dom', () => ({
  useNavigate: jest.fn(),
}));

// Test component that uses the useNavigation hook.
const TestComponent = ({ onTest }) => {
  const navigate = useNavigation();
  // Pass the obtained navigate to a callback so we can test it.
  onTest && onTest(navigate);
  return <div data-testid="nav-result">{navigate ? 'present' : 'absent'}</div>;
};

describe('NavigationContext', () => {
  it('provides a navigate function to consumers via useNavigation', () => {
    // Create a fake navigate function.
    const fakeNavigate = jest.fn();
    useNavigate.mockReturnValue(fakeNavigate);
    
    render(
      <NavigationProvider>
        <TestComponent />
      </NavigationProvider>
    );
    
    // The test component should render "present" indicating a non-null navigate function.
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
