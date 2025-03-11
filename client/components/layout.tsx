"use client"

import type { ReactNode } from "react"
import { useRouter, usePathname } from "next/navigation"
import { Home, Users, User, Wallet } from "lucide-react"
import { Button } from "@/components/ui/button"

export default function Layout({ children }: { children: ReactNode }) {
  const router = useRouter()
  const pathname = usePathname()

  return (
    <div className="max-w-md mx-auto min-h-screen flex flex-col bg-gradient-to-b from-blue-50 to-white">
      {children}

      {/* Footer Navigation */}
      <footer className="fixed bottom-0 left-0 right-0 bg-white border-t">
        <nav className="max-w-md mx-auto grid grid-cols-4 gap-1 py-1">
          <Button
            variant="ghost"
            size="sm"
            className={`flex flex-col items-center justify-center ${pathname === "/" ? "text-blue-500" : "text-gray-600 hover:text-blue-500"}`}
            onClick={() => router.push("/")}
          >
            <Home className="h-5 w-5" />
            <span className="text-xs mt-1">홈</span>
          </Button>
          <Button
            variant="ghost"
            size="sm"
            className={`flex flex-col items-center justify-center ${pathname.startsWith("/group") ? "text-blue-500" : "text-gray-600 hover:text-blue-500"}`}
            onClick={() => router.push("/group")}
          >
            <Users className="h-5 w-5" />
            <span className="text-xs mt-1">모임</span>
          </Button>
          <Button
            variant="ghost"
            size="sm"
            className={`flex flex-col items-center justify-center ${pathname === "/profile" ? "text-blue-500" : "text-gray-600 hover:text-blue-500"}`}
            onClick={() => router.push("/profile")}
          >
            <User className="h-5 w-5" />
            <span className="text-xs mt-1">내정보</span>
          </Button>
          <Button
            variant="ghost"
            size="sm"
            className={`flex flex-col items-center justify-center ${pathname === "/finance" ? "text-blue-500" : "text-gray-600 hover:text-blue-500"}`}
            onClick={() => router.push("/finance")}
          >
            <Wallet className="h-5 w-5" />
            <span className="text-xs mt-1">금융</span>
          </Button>
        </nav>
      </footer>
    </div>
  )
}

