/**
 * Error Logger Utility
 * Author: FullStack-Developer-AI (Cursor)
 * Created: June 30, 2025
 * Version: 1.0
 * Context: Comprehensive error logging for module resolution and runtime errors
 */

// Error types
const ERROR_TYPES = {
  MODULE_NOT_FOUND: 'MODULE_NOT_FOUND',
  API_ERROR: 'API_ERROR',
  AUTHENTICATION_ERROR: 'AUTHENTICATION_ERROR',
  VALIDATION_ERROR: 'VALIDATION_ERROR',
  RUNTIME_ERROR: 'RUNTIME_ERROR',
  NETWORK_ERROR: 'NETWORK_ERROR'
};

// Log levels
const LOG_LEVELS = {
  ERROR: 'ERROR',
  WARN: 'WARN',
  INFO: 'INFO',
  DEBUG: 'DEBUG'
};

/**
 * Enhanced Error Logger Class
 */
class ErrorLogger {
  constructor() {
    this.isDevelopment = process.env.NODE_ENV === 'development';
    this.logs = [];
    this.setupGlobalErrorHandlers();
  }

  /**
   * Setup global error handlers for unhandled errors
   */
  setupGlobalErrorHandlers() {
    // Capture unhandled promise rejections
    window.addEventListener('unhandledrejection', (event) => {
      this.logError({
        type: ERROR_TYPES.RUNTIME_ERROR,
        message: 'Unhandled Promise Rejection',
        error: event.reason,
        stack: event.reason?.stack,
        timestamp: new Date().toISOString()
      });
    });

    // Capture global JavaScript errors
    window.addEventListener('error', (event) => {
      this.logError({
        type: ERROR_TYPES.RUNTIME_ERROR,
        message: event.message,
        filename: event.filename,
        lineno: event.lineno,
        colno: event.colno,
        error: event.error,
        stack: event.error?.stack,
        timestamp: new Date().toISOString()
      });
    });
  }

  /**
   * Log error with detailed information
   */
  logError(errorInfo) {
    const errorLog = {
      level: LOG_LEVELS.ERROR,
      timestamp: new Date().toISOString(),
      url: window.location.href,
      userAgent: navigator.userAgent,
      ...errorInfo
    };

    // Store in memory
    this.logs.push(errorLog);

    // Console logging in development
    if (this.isDevelopment) {
      console.group(`🚨 ${errorInfo.type || 'ERROR'} - ${errorInfo.message}`);
      console.error('Error Details:', errorLog);
      if (errorInfo.stack) {
        console.error('Stack Trace:', errorInfo.stack);
      }
      console.groupEnd();
    }

    // Send to backend in production (optional)
    if (!this.isDevelopment) {
      this.sendToBackend(errorLog);
    }

    return errorLog;
  }

  /**
   * Log module resolution errors specifically
   */
  logModuleError(moduleName, path, component) {
    return this.logError({
      type: ERROR_TYPES.MODULE_NOT_FOUND,
      message: `Cannot resolve module '${moduleName}'`,
      moduleName,
      path,
      component,
      suggestion: 'Check if the file exists or if the import path is correct'
    });
  }

  /**
   * Log API errors
   */
  logApiError(url, status, message, response) {
    return this.logError({
      type: ERROR_TYPES.API_ERROR,
      message: `API Error: ${message}`,
      url,
      status,
      response,
      suggestion: 'Check API endpoint and network connectivity'
    });
  }

  /**
   * Log authentication errors
   */
  logAuthError(action, message) {
    return this.logError({
      type: ERROR_TYPES.AUTHENTICATION_ERROR,
      message: `Auth Error: ${message}`,
      action,
      suggestion: 'Check authentication token and user permissions'
    });
  }

  /**
   * Send error to backend for persistent logging
   */
  async sendToBackend(errorLog) {
    try {
      const token = localStorage.getItem('token');
      await fetch('http://localhost:8080/api/logs/frontend-error', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token ? `Bearer ${token}` : ''
        },
        body: JSON.stringify(errorLog)
      });
    } catch (error) {
      console.warn('Failed to send error log to backend:', error);
    }
  }

  /**
   * Get all stored logs
   */
  getLogs() {
    return this.logs;
  }

  /**
   * Clear logs
   */
  clearLogs() {
    this.logs = [];
  }

  /**
   * Export logs as JSON
   */
  exportLogs() {
    const blob = new Blob([JSON.stringify(this.logs, null, 2)], {
      type: 'application/json'
    });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `error-logs-${new Date().toISOString().split('T')[0]}.json`;
    a.click();
    URL.revokeObjectURL(url);
  }

  /**
   * Development helpers
   */
  debugInfo() {
    if (!this.isDevelopment) return;
    
    console.group('🔍 Error Logger Debug Info');
    console.log('Total logs:', this.logs.length);
    console.log('Recent errors:', this.logs.slice(-5));
    console.log('Error types:', this.getErrorStats());
    console.groupEnd();
  }

  getErrorStats() {
    const stats = {};
    this.logs.forEach(log => {
      stats[log.type] = (stats[log.type] || 0) + 1;
    });
    return stats;
  }
}

// Create global instance
const errorLogger = new ErrorLogger();

// Convenience functions
export const logModuleError = (moduleName, path, component) => 
  errorLogger.logModuleError(moduleName, path, component);

export const logApiError = (url, status, message, response) => 
  errorLogger.logApiError(url, status, message, response);

export const logAuthError = (action, message) => 
  errorLogger.logAuthError(action, message);

export const logError = (errorInfo) => 
  errorLogger.logError(errorInfo);

export const getErrorLogs = () => 
  errorLogger.getLogs();

export const clearErrorLogs = () => 
  errorLogger.clearLogs();

export const exportErrorLogs = () => 
  errorLogger.exportLogs();

export const debugErrorLogger = () => 
  errorLogger.debugInfo();

export default errorLogger; 