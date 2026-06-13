import { Controller, Get, Post, Req, Res } from '@nestjs/common';
import { Request, Response } from 'express';

@Controller()
export class AppController {
  @Get('ping')
  ping() {
    return { ok: true };
  }

  @Get('form')
  getForm(@Req() req: Request) {
    // csurf exposes req.csrfToken()
    const token = (req as any).csrfToken ? (req as any).csrfToken() : null;
    return { csrfToken: token };
  }

  @Post('submit')
  submit(@Req() req: Request, @Res() res: Response) {
    // If CSRF fails, middleware will have already thrown 403.
    res.json({ received: true });
  }
}
