import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';
import helmet = require('helmet');
import cookieParser = require('cookie-parser');
import csurf = require('csurf');

describe('Rate limit e2e', () => {
  let app: INestApplication;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.use(helmet());
    app.use(cookieParser());
    app.use(csurf({ cookie: true }) as any);
    await app.init();
  });

  afterAll(async () => {
    await app.close();
  });

  it('allows 100 requests and blocks the 101st with 429', async () => {
    const agent = request.agent(app.getHttpServer());

    // Send 100 requests
    for (let i = 0; i < 100; i++) {
      const res = await agent.get('/ping');
      expect(res.status).toBe(200);
    }

    // 101st should be throttled
    const res = await agent.get('/ping');
    expect(res.status).toBe(429);
  }, 20000);
});
