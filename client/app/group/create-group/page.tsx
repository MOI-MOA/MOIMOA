"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardContent } from "@/components/ui/card";
import { toast } from "@/components/ui/use-toast";
import { Header } from "@/components/Header";

interface GatheringData {
  gatheringTitle: string;
  gatheringIntroduction: string;
  memberCount: number;
  basicFee: number;
}

interface ApiResponse {
  httpStatus: number;
  message: string;
  datas: GatheringData[];
}

export default function CreateGroupPage() {
  const router = useRouter();
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    gatheringTitle: "",
    gatheringIntroduction: "",
    memberCount: "",
    basicFee: "",
  });
  const [isLoading, setIsLoading] = useState(false);

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;

    // monthlyDepositDay 입력값 검증
    if (name === "monthlyDepositDay") {
      const numValue = parseInt(value);
      if (numValue < 1 || numValue > 31) {
        return; // 유효하지 않은 값이면 상태 업데이트하지 않음
      }
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleNext = () => {
    // 각 단계별 필수 입력값 검증
    switch (step) {
      case 1:
        if (
          !formData.gatheringTitle ||
          !formData.gatheringIntroduction ||
          !formData.memberCount
        ) {
          toast({
            title: "필수 입력",
            description: "모임 이름, 소개, 참여 인원을 모두 입력해주세요.",
            variant: "destructive",
          });
          return;
        }
        break;
      case 2:
        if (!formData.basicFee) {
          toast({
            title: "필수 입력",
            description: "기본 회비를 입력해주세요.",
            variant: "destructive",
          });
          return;
        }
        break;
    }

    if (step < 2) {
      setStep((prev) => prev + 1);
    }
  };

  const handlePrevious = () => {
    if (step > 1) {
      setStep((prev) => prev - 1);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      const response = await fetch("/api/v1/gathering", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          gatheringTitle: formData.gatheringTitle,
          gatheringIntroduction: formData.gatheringIntroduction,
          memberCount: parseInt(formData.memberCount),
          basicFee: parseInt(formData.basicFee),
        }),
      });

      const data: ApiResponse = await response.json();

      if (data.httpStatus === 200) {
        toast({
          title: "모임 생성 성공",
          description: data.message,
        });
        router.push("/group");
      } else {
        toast({
          title: "모임 생성 실패",
          description: data.message,
          variant: "destructive",
        });
      }
    } catch (error) {
      console.error("그룹 생성 실패:", error);
      toast({
        title: "오류 발생",
        description: "모임 생성 중 오류가 발생했습니다.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const renderStep = () => {
    switch (step) {
      case 1:
        return (
          <>
            <div>
              <Label htmlFor="gatheringTitle">모임 이름</Label>
              <Input
                id="gatheringTitle"
                name="gatheringTitle"
                value={formData.gatheringTitle}
                onChange={handleInputChange}
                placeholder="모임 이름을 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="gatheringIntroduction">모임 소개</Label>
              <Textarea
                id="gatheringIntroduction"
                name="gatheringIntroduction"
                value={formData.gatheringIntroduction}
                onChange={handleInputChange}
                placeholder="모임에 대한 간단한 소개를 입력하세요"
                required
              />
            </div>
            <div>
              <Label htmlFor="memberCount">참여 인원</Label>
              <Input
                id="memberCount"
                name="memberCount"
                type="number"
                value={formData.memberCount}
                onChange={handleInputChange}
                placeholder="참여 인원 수를 입력하세요"
                required
              />
            </div>
          </>
        );
      case 2:
        return (
          <>
            <div>
              <Label htmlFor="basicFee">기본 회비</Label>
              <Input
                id="basicFee"
                name="basicFee"
                type="number"
                value={formData.basicFee}
                onChange={handleInputChange}
                placeholder="기본 회비를 입력하세요"
                required
              />
            </div>
          </>
        );
      default:
        return null;
    }
  };

  return (
    <>
      <Header title="새 모임 만들기" showBackButton />
      <main className="flex-1 overflow-auto p-4 pb-16">
        <Card>
          <CardContent className="p-6">
            <div className="mb-6 text-center">
              <h2 className="text-xl font-semibold">Step {step} of 2</h2>
              <div className="w-full bg-gray-200 rounded-full h-2.5 mt-2">
                <div
                  className="bg-blue-600 h-2.5 rounded-full"
                  style={{ width: `${(step / 2) * 100}%` }}
                ></div>
              </div>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4">
              {renderStep()}
              <div className="flex justify-between mt-6">
                {step > 1 && (
                  <Button
                    type="button"
                    onClick={handlePrevious}
                    disabled={isLoading}
                  >
                    이전
                  </Button>
                )}
                {step < 2 ? (
                  <Button
                    type="button"
                    onClick={handleNext}
                    className="ml-auto"
                    disabled={isLoading}
                  >
                    다음
                    <ArrowRight className="h-4 w-4 ml-2" />
                  </Button>
                ) : (
                  <Button
                    type="submit"
                    className="ml-auto bg-blue-500 hover:bg-blue-600 text-white"
                    disabled={isLoading}
                  >
                    {isLoading ? "처리 중..." : "모임 만들기"}
                  </Button>
                )}
              </div>
            </form>
          </CardContent>
        </Card>
      </main>
    </>
  );
}
