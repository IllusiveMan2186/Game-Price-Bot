import React from 'react';
import { renderHook, act } from '@testing-library/react-hooks';
import { AuthProvider, useAuth } from '../AuthContext';

const wrapper = ({ children }) => <AuthProvider>{children}</AuthProvider>;

describe('AuthContext', () => {
  beforeEach(() => {
    window.localStorage.clear();
  });

  it('should getAccessToken returns value from localStorage', () => {
    window.localStorage.setItem('AUTH_TOKEN', 'foo');
    const { result } = renderHook(() => useAuth(), { wrapper });
    expect(result.current.getAccessToken()).toBe('foo');
  });

  it('should setAccessToken sets AUTH_TOKEN and removes LINK_TOKEN', () => {
    window.localStorage.setItem('LINK_TOKEN', 'some-link-token');
    const { result } = renderHook(() => useAuth(), { wrapper });
    act(() => {
      result.current.setAccessToken('bar');
    });
    expect(window.localStorage.getItem('AUTH_TOKEN')).toBe('bar');
    expect(window.localStorage.getItem('LINK_TOKEN')).toBeNull();
  });

  it('should setAccessToken with null removes AUTH_TOKEN', () => {
    window.localStorage.setItem('AUTH_TOKEN', 'bar');
    const { result } = renderHook(() => useAuth(), { wrapper });
    act(() => {
      result.current.setAccessToken(null);
    });
    expect(window.localStorage.getItem('AUTH_TOKEN')).toBeNull();
  });

  it('should getLinkToken and setLinkToken work correctly', () => {
    const { result } = renderHook(() => useAuth(), { wrapper });
    act(() => {
      result.current.setLinkToken('link123');
    });
    expect(result.current.getLinkToken()).toBe('link123');
  });

  it('should getUserRole and setUserRole work correctly', () => {
    const { result } = renderHook(() => useAuth(), { wrapper });
    act(() => {
      result.current.setUserRole('ROLE_USER');
    });
    expect(result.current.getUserRole()).toBe('ROLE_USER');
  });

  it('should logout clears tokens and calls navigate with 0', () => {
    // Pre-set tokens in localStorage.
    window.localStorage.setItem('AUTH_TOKEN', 'bar');
    window.localStorage.setItem('LINK_TOKEN', 'link');
    window.localStorage.setItem('USER_ROLE', 'ROLE_ADMIN');
    const navigate = jest.fn();
    const { result } = renderHook(() => useAuth(), { wrapper });
    act(() => {
      result.current.logout(navigate);
    });
    expect(window.localStorage.getItem('AUTH_TOKEN')).toBeNull();
    expect(window.localStorage.getItem('LINK_TOKEN')).toBeNull();
    expect(window.localStorage.getItem('USER_ROLE')).toBeNull();
    expect(navigate).toHaveBeenCalledWith(0);
  });

  it('should isUserAuth returns true when AUTH_TOKEN exists and false otherwise', () => {
    const { result, rerender } = renderHook(() => useAuth(), { wrapper });
    // Initially, no token exists.
    expect(result.current.isUserAuth()).toBe(false);
    act(() => {
      window.localStorage.setItem('AUTH_TOKEN', 'foo');
    });
    // Rerender to re-read localStorage.
    rerender();
    expect(result.current.isUserAuth()).toBe(true);
  });

  it('should isUserAdmin returns true if USER_ROLE is ROLE_ADMIN and false otherwise', () => {
    const { result, rerender } = renderHook(() => useAuth(), { wrapper });
    act(() => {
      window.localStorage.setItem('USER_ROLE', 'ROLE_ADMIN');
    });
    rerender();
    expect(result.current.isUserAdmin()).toBe(true);
    act(() => {
      window.localStorage.setItem('USER_ROLE', 'ROLE_USER');
    });
    rerender();
    expect(result.current.isUserAdmin()).toBe(false);
  });
});
