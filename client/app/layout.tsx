import type React from "react";
import type { Metadata } from "next";
import { Inter } from "next/font/google";
import { ThemeProvider } from "@/components/theme-provider";
import { Toaster } from "@/components/ui/toaster";
import { Footer } from "@/components/Footer";
import { AuthProvider } from "./context/AuthContext";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "MOIMO",
  description: "MOIMO - 간편한 모임비 관리",
  icons: {
    icon: "/우리어플 아이콘.png",
    shortcut: "/우리어플 아이콘.png",
    apple: "/우리어플 아이콘.png",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" suppressHydrationWarning>
      <body className={inter.className} suppressHydrationWarning>
        <ThemeProvider
          attribute="class"
          defaultTheme="light"
          enableSystem={false}
        >
          <AuthProvider>
            <div className="max-w-md mx-auto min-h-screen flex flex-col bg-gradient-to-b from-blue-50 to-white">
              {children}
              <Footer />
            </div>
          </AuthProvider>
          <Toaster />
        </ThemeProvider>
      </body>
    </html>
  );
}
