"use client"
import { ThemeProvider as NextThemesProvider } from "next-themes"
import type { ThemeProviderProps } from "next-themes"
import { useEffect, useState } from "react"

export function ThemeProvider({ children, ...props }: ThemeProviderProps) {
  // 클라이언트 사이드 렌더링을 위한 마운트 상태 추가
  const [mounted, setMounted] = useState(false)

  // 컴포넌트가 마운트된 후에만 렌더링
  useEffect(() => {
    setMounted(true)
  }, [])

  // 마운트되기 전에는 children만 렌더링하여 hydration 불일치 방지
  if (!mounted) {
    return <>{children}</>
  }

  return <NextThemesProvider {...props}>{children}</NextThemesProvider>
}

