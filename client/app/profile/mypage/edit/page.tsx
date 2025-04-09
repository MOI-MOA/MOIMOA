"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Header } from "@/components/Header";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
  CardFooter,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { toast } from "@/components/ui/use-toast";
import { Camera, User, Save, X, ArrowLeft } from "lucide-react";
import { authApi } from "@/lib/api";

export default function EditProfilePage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const nameParam = searchParams.get("name");

  // 사용자 정보 (실제로는 API에서 가져와야 함)
  const [userInfo, setUserInfo] = useState({
    name: nameParam || "",
    email: "user@example.com",
    phone: "010-1234-5678",
    avatar: "/placeholder.svg?height=100&width=100",
  });
  
  const [isLoading, setIsLoading] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setUserInfo((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSaveProfile = async () => {
    setIsLoading(true);
    try {
      await authApi.patch("/api/v1/profile", {
        name: userInfo.name,
      });
      toast({
        title: "프로필 저장 완료",
        description: "프로필 정보가 성공적으로 저장되었습니다.",
      });
      router.push("/profile/mypage");
    } catch (error) {
      console.error("프로필 저장 실패:", error);
      toast({
        title: "오류",
        description: "프로필 정보 저장에 실패했습니다.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <Header title="프로필 수정" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16 bg-slate-50">
        <div className="space-y-3">
          <h2 className="text-xl font-semibold text-slate-800 flex items-center">
            <User className="h-5 w-5 mr-2 text-blue-600" />
            프로필 수정
          </h2>
          
          <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
            <CardContent className="p-0">
              <div className="bg-gradient-to-r from-blue-500 to-indigo-500 h-32 flex items-center justify-center">
                <div className="relative">
                  <Avatar className="h-32 w-32 border-4 border-white shadow-md">
                    <AvatarImage src={userInfo.avatar} />
                    <AvatarFallback className="bg-blue-100 text-blue-600 text-3xl">
                      {userInfo.name.slice(0, 2)}
                    </AvatarFallback>
                  </Avatar>
                  <Button
                    variant="secondary"
                    size="sm"
                    className="absolute bottom-0 right-0 rounded-full p-2 bg-white shadow-md hover:bg-slate-100"
                  >
                    <Camera className="h-4 w-4 text-slate-700" />
                  </Button>
                </div>
              </div>
              
              <div className="p-6 pt-8">
                <div className="space-y-6">
                  <div className="space-y-3">
                    <Label htmlFor="name" className="text-slate-700 font-medium block">
                      이름
                    </Label>
                    <div className="relative">
                      <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                        <User className="h-5 w-5 text-slate-400" />
                      </div>
                      <Input
                        id="name"
                        name="name"
                        value={userInfo.name}
                        onChange={handleInputChange}
                        placeholder="이름을 입력하세요"
                        className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                      />
                    </div>
                  </div>
                  
                  <div className="flex flex-col md:flex-row md:space-x-3 space-y-3 md:space-y-0 pt-4">
                    <Button
                      variant="outline"
                      className="rounded-xl border-slate-200 hover:bg-slate-100 text-slate-700 py-6"
                      onClick={() => router.back()}
                    >
                      <ArrowLeft className="h-4 w-4 mr-2" />
                      취소
                    </Button>
                    <Button 
                      className="rounded-xl bg-blue-600 hover:bg-blue-700 py-6 flex-1"
                      onClick={handleSaveProfile}
                      disabled={isLoading}
                    >
                      <Save className="h-4 w-4 mr-2" />
                      {isLoading ? "저장 중..." : "저장하기"}
                    </Button>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
        
        <div className="text-center text-sm text-slate-500 mt-8">
          <p>프로필 사진은 언제든지 변경할 수 있습니다.</p>
          <p>개인정보는 안전하게 보호됩니다.</p>
        </div>
      </main>
    </>
  );
}
