"use client"

import { useRouter } from "next/navigation"
import { ArrowLeft } from "lucide-react"
import { Button } from "@/components/ui/button"

interface HeaderProps {
  title?: string
  showBackButton?: boolean
}

export function Header({ title, showBackButton = false }: HeaderProps) {
  const router = useRouter()

  return (
    <header className="flex justify-between items-center p-4 bg-white border-b">
      <div className="flex items-center">
        {showBackButton && (
          <Button variant="ghost" onClick={() => router.back()}>
            <ArrowLeft className="h-5 w-5" />
          </Button>
        )}
        {!showBackButton && (
          <svg
            className="h-8 w-8 text-blue-500"
            fill="none"
            height="24"
            stroke="currentColor"
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="2"
            viewBox="0 0 24 24"
            width="24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
            <polyline points="9 22 9 12 15 12 15 22" />
          </svg>
        )}
        {title && <h1 className="text-xl font-semibold ml-2">{title}</h1>}
      </div>
      <div className="flex space-x-2">
        <Button variant="ghost" size="sm" onClick={() => router.push("/signup")}>
          회원가입
        </Button>
        <Button variant="outline" size="sm" onClick={() => router.push("/login")}>
          로그인
        </Button>
      </div>
    </header>
  )
}

