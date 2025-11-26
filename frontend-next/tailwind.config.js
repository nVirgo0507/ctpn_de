/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
        "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
        "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
    ],
    theme: {
        extend: {
            fontFamily: {
                'sans': ['Inter', 'Roboto', 'system-ui', 'sans-serif'],
                'vietnamese': ['Inter', 'Roboto', 'system-ui']
            },
            colors: {
                // Glassmorphism palette
                glass: {
                    50: 'rgba(255, 255, 255, 0.1)',
                    100: 'rgba(255, 255, 255, 0.2)',
                    200: 'rgba(255, 255, 255, 0.3)',
                    border: 'rgba(255, 255, 255, 0.2)',
                    shadow: 'rgba(0, 0, 0, 0.1)'
                },
                primary: {
                    50: '#eff6ff',
                    100: '#dbeafe',
                    500: '#2563eb',
                    600: '#1d4ed8',
                    700: '#1e40af'
                },
                success: {
                    50: '#ecfdf5',
                    100: '#d1fae5',
                    500: '#10b981',
                    600: '#059669'
                },
                warning: {
                    50: '#fffbeb',
                    100: '#fef3c7',
                    500: '#f59e0b',
                    600: '#d97706'
                },
                danger: {
                    50: '#fef2f2',
                    100: '#fee2e2',
                    500: '#ef4444',
                    600: '#dc2626'
                }
            },
            backdropBlur: {
                'glass': '12px'
            },
            borderRadius: {
                'glass': '16px'
            },
            boxShadow: {
                'glass': '0 8px 32px rgba(0, 0, 0, 0.1)',
                'glass-hover': '0 12px 40px rgba(0, 0, 0, 0.15)'
            },
            keyframes: {
                blob: {
                    "0%": {
                        transform: "translate(0px, 0px) scale(1)",
                    },
                    "33%": {
                        transform: "translate(30px, -50px) scale(1.1)",
                    },
                    "66%": {
                        transform: "translate(-20px, 20px) scale(0.9)",
                    },
                    "100%": {
                        transform: "translate(0px, 0px) scale(1)",
                    },
                },
                "fade-in-up": {
                    "0%": {
                        opacity: "0",
                        transform: "translateY(20px)",
                    },
                    "100%": {
                        opacity: "1",
                        transform: "translateY(0)",
                    },
                },
                "fade-in-down": {
                    "0%": {
                        opacity: "0",
                        transform: "translateY(-20px)",
                    },
                    "100%": {
                        opacity: "1",
                        transform: "translateY(0)",
                    },
                },
            },
            animation: {
                blob: "blob 7s infinite",
                "fade-in-up": "fade-in-up 0.5s ease-out",
                "fade-in-down": "fade-in-down 0.5s ease-out",
            },
        },
    },
    plugins: [],
};
