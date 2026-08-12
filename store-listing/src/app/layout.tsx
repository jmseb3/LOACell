import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "LoaCell Store Screenshots",
  description: "LoaCell App Store and Google Play screenshot generator",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  );
}
